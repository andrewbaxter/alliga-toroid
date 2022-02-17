package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialValue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class ModuleCompileContext {
  public final ImportId importId;
  public final CompileContext compileContext;
  /**
   * TODO if module generates method that does evaluation then calls it in parallel, this will have
   * concurrent access. This should probably be moved into evaluation context then compiled into
   * module context thread safe post-evaluation (?)
   */
  public final TSList<Error> errors = new TSList<>();

  public final ImportPath importPath;
  /** Already desemiserialized artifacts generated via imports. */
  public final TSMap<ArtifactId, Exportable> artifactLookup = new TSMap<>();
  /**
   * A mapping of artifacts imported, used while semiserializing output after compilation to prevent
   * re-semiserializing artifacts from other modules.
   */
  public final TSMap<ObjId<Exportable>, ArtifactId> backArtifactLookup = new TSMap<>();

  private final TSMap<ImportId, CompletableFuture<Value>> modules = new TSMap<>();

  public ModuleCompileContext(
      ImportId importId, CompileContext compileContext, ImportPath importPath) {
    this.importId = importId;
    this.compileContext = compileContext;
    this.importPath = importPath;
  }

  public Object lookupRef(SemiserialRef ref) {
    return ref.dispatchRef(
        new SemiserialRef.Dispatcher<Object>() {
          @Override
          public Object handleArtifact(SemiserialRefArtifact s) {
            return artifactLookup.get(s.id);
          }

          @Override
          public Object handleBuiltin(SemiserialRefBuiltin s) {
            return Meta.semiKeyToBuiltin.get(s.key);
          }
        });
  }

  public Value desemiserialize(SemiserialModule semi, ImportId importId) {
    TSList<ROPair<ArtifactId, SemiserialValue>> remaining =
        new TSList<ROPair<ArtifactId, SemiserialValue>>();
    for (int i = 0; i < semi.artifacts.size(); ++i) {
      final SemiserialValue semiValue = semi.artifacts.get(i);
      final ArtifactId artifactId = new ArtifactId(importId, i);
      remaining.add(new ROPair<>(artifactId, semiValue));
    }
    // type, (id, semivalue)
    TSList<ROPair<IdentityExportableType, ROPair<ArtifactId, SemiserialValue>>> stratum = new TSList<>();
    do {
      stratum.clear();
      final Iterator<ROPair<ArtifactId, SemiserialValue>> iter = remaining.iterator();

      // Find next subset of artifacts whose types are all resolved
      while (iter.hasNext()) {
        final ROPair<ArtifactId, SemiserialValue> pair = iter.next();
        final SemiserialValue semiValue = pair.second;
        IdentityExportableType type = (IdentityExportableType) lookupRef(semiValue.type);
        if (type != null) {
          stratum.add(new ROPair<>(type, pair));
          iter.remove();
        }
      }

      // Desemiserialize this stratum
      Desemiserializer typeDesemiserializer = new Desemiserializer();
      for (ROPair<IdentityExportableType, ROPair<ArtifactId, SemiserialValue>> candidate : stratum) {
        final Exportable value =
            (Exportable)
                candidate.first.graphDesemiserializeArtifact(
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
    return out;
  }

  public CompletableFuture<Value> getModule(ImportId importId) {
    return modules.getCreate(
        importId,
        () -> {
          return compileContext
              .modules
              .getModule(compileContext, importPath, importId)
              .thenApply(
                  semi -> {
                    return desemiserialize(semi.result(), importId);
                  });
        });
  }
}
