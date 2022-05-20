package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRefIdentity;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialExportableIdentityBody;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.modules.CacheImportIdRes;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleCompileContext {
  public final ImportId importId;
  public final long importCacheId;
  public final AtomicInteger nextLocalId = new AtomicInteger();
  public final CompileContext compileContext;

  public final ImportPath importPath;
  /** Already desemiserialized artifacts generated via imports. */
  public final TSMap<ArtifactId, Object> artifactLookup = new TSMap<>();
  /**
   * A mapping of artifacts imported, used while semiserializing output after compilation to prevent
   * re-semiserializing artifacts from other modules.
   */
  public final TSMap<ObjId<IdentityExportable>, ArtifactId> backArtifactLookup = new TSMap<>();

  public final TSList<ROPair<Location, CompletableFuture>> deferredErrors = new TSList<>();
  public final TSList<Error> errors = new TSList<>();
  public final TSList<String> log = new TSList<>();
  private final TSMap<Long, CompletableFuture<Value>> modules = new TSMap<>();

  public ModuleCompileContext(
          ImportId importId, long importCacheId, CompileContext compileContext, ImportPath importPath) {
    this.importId = importId;
    this.importCacheId = importCacheId;
    this.compileContext = compileContext;
    this.importPath = importPath;
  }

  public Object lookupRef(SemiserialSubvalueRef ref) {
    return ref.dispatchExportable(
        new SemiserialSubvalueRef.Dispatcher<Object>() {
          @Override
          public Object handleArtifact(SemiserialSubvalueRefIdentity s) {
            return artifactLookup.get(s.id);
          }

          @Override
          public Object handleBuiltin(SemiserialSubvalueRefBuiltin s) {
            return Meta.semiKeyToBuiltin.get(s.key);
          }
        });
  }

  public Value desemiserialize(SemiserialModule semi, CacheImportIdRes cacheImportId) {
    TSList<ROPair<ArtifactId, SemiserialExportableIdentityBody>> remaining =
        new TSList<ROPair<ArtifactId, SemiserialExportableIdentityBody>>();
    for (int i = 0; i < semi.artifacts.size(); ++i) {
      final SemiserialExportableIdentityBody semiValue = semi.artifacts.get(i);
      final ArtifactId artifactId = ArtifactId.create(cacheImportId.cacheId, i);
      remaining.add(new ROPair<>(artifactId, semiValue));
    }
    // type, (id, semivalue)
    // Only identity exportables get flattened into artifacts - all others are either builtin (no artifact)
    // or inline.
    TSList<ROPair<IdentityExportableType, ROPair<ArtifactId, SemiserialExportableIdentityBody>>> stratum =
        new TSList<>();
    do {
      stratum.clear();
      final Iterator<ROPair<ArtifactId, SemiserialExportableIdentityBody>> iter = remaining.iterator();

      // Find next subset of artifacts whose types are all resolved
      while (iter.hasNext()) {
        final ROPair<ArtifactId, SemiserialExportableIdentityBody> pair = iter.next();
        final SemiserialExportableIdentityBody semiValue = pair.second;
        IdentityExportableType type = (IdentityExportableType) lookupRef(semiValue.type);
        if (type != null) {
          stratum.add(new ROPair<>(type, pair));
          iter.remove();
        }
      }

      // Desemiserialize this stratum
      Desemiserializer typeDesemiserializer = new Desemiserializer();
      for (ROPair<IdentityExportableType, ROPair<ArtifactId, SemiserialExportableIdentityBody>> candidate :
          stratum) {
        final IdentityExportable value =
                candidate.first.graphDesemiserializeBody(
                    this, typeDesemiserializer, candidate.second.second.data);
        artifactLookup.put(candidate.second.first, value);
        backArtifactLookup.put(new ObjId<>(value), candidate.second.first);
      }
      typeDesemiserializer.finish();
    } while (stratum.some());
    if (remaining.some()) {
      /**
       * Type dependency loop. Probbly needs user-facing error but this shouldn't ever happen - it
       * should be cought during initial semiserialization.
       */
      throw new Assertion();
    }
    final Value out = (Value) lookupRef(semi.root);
    if (out == null) {
      /** Shouldn't happen unless someone messes with the cache data directly. */
      throw new Assertion();
    }
    // DEBUG
    /*
    {
      TSMap<String, Integer> counts = new TSMap<>();
      TSMap<Integer, Integer> aiCounts = new TSMap<>();
      for (Map.Entry<ObjId<Exportable>, ArtifactId> e : backArtifactLookup) {
        if (!e.getValue().spec.equal1(importId)) continue;
        String name = e.getKey().objId.getClass().getCanonicalName();
        counts.putReplace(name, counts.getCreate(name, () -> 0) + 1);
        aiCounts.putReplace(
            e.getValue().index, aiCounts.getCreate(e.getValue().index, () -> 0) + 1);
      }
      for (Map.Entry<String, Integer> count : counts) {
        System.out.format("count %s -> %s\n", count.getKey(), count.getValue());
      }
      for (Map.Entry<Integer, Integer> count : aiCounts) {
        if (count.getValue() > 1)
          System.out.format("artifact id %s -> %s\n", count.getKey(), count.getValue());
      }
    }
    System.out.format(
        "back artifact lookup %s %s %d\n",
        importPath, importId.moduleId, backArtifactLookup.size());
     */
    // DEBUG
    return out;
  }

  public CompletableFuture<Value> getModule(CacheImportIdRes cacheImportId) {
    return modules.getCreate(
        cacheImportId.cacheId,
        () -> {
          return compileContext
              .modules
              .getModule(compileContext, importPath, cacheImportId)
              .thenApply(
                  semi -> {
                    return desemiserialize(semi.result(), cacheImportId);
                  });
        });
  }

  public TSList<Error> getErrors() {
    return errors;
  }
}
