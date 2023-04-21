package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBuiltinRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialExportableRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.modules.CacheImportIdRes;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
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
  public final TSMap<ObjId<Object>, ArtifactId> backArtifactLookup = new TSMap<>();

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

  public Object lookupRef(SemiserialRef ref) {
    return ref.dispatchExportable(
        new SemiserialRef.Dispatcher<Object>() {
          @Override
          public Object handleExportableRef(SemiserialExportableRef s) {
            return artifactLookup.get(s.id);
          }

          @Override
          public Object handleBuiltinRef(SemiserialBuiltinRef s) {
            return StaticAutogen.singletonExportableLookup.get(s.index);
          }
        });
  }

  public Value desemiserialize(SemiserialModule semi, CacheImportIdRes cacheImportId) {
    TSList<ROPair<ArtifactId, SemiserialExportable>> remaining =
        new TSList<ROPair<ArtifactId, SemiserialExportable>>();
    for (int i = 0; i < semi.artifacts.size(); ++i) {
      final SemiserialExportable semiValue = semi.artifacts.get(i);
      final ArtifactId artifactId = ArtifactId.create(cacheImportId.cacheId, i);
      remaining.add(new ROPair<>(artifactId, semiValue));
    }
    // type, (id, semivalue)
    // Only identity exportables get flattened into artifacts - all others are either builtin (no
    // artifact)
    // or inline.
    TSList<ROPair<Exporter, ROPair<ArtifactId, SemiserialExportable>>> stratum =
        new TSList<>();
    do {
      stratum.clear();
      final Iterator<ROPair<ArtifactId, SemiserialExportable>> iter = remaining.iterator();

      // Find next subset of artifacts whose types are all resolved
      while (iter.hasNext()) {
        final ROPair<ArtifactId, SemiserialExportable> pair = iter.next();
        final SemiserialExportable semiValue = pair.second;
        Exporter type = (Exporter) lookupRef(semiValue.type);
        if (type != null) {
          stratum.add(new ROPair<>(type, pair));
          iter.remove();
        }
      }

      // Desemiserialize this stratum
      Desemiserializer typeDesemiserializer = new Desemiserializer();
      for (ROPair<Exporter, ROPair<ArtifactId, SemiserialExportable>> candidate : stratum) {
        final Object value =
            candidate.first.graphDesemiserializeBody(
                this, typeDesemiserializer, candidate.second.second.value);
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
