package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.Asyncer;
import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialValue;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class Modules {
  private final Asyncer<ImportId, Module> modules;
  private final ModuleResolver inner;

  public Modules(ModuleResolver inner) {
    this.inner = inner;
    modules = new Asyncer<>();
  }

  private CompletableFuture<Module> getModule(
      CompileContext context, ImportPath fromImportPath, ImportId importId) {
    return modules.get(
        context.threads,
        importId,
        () -> {
          final Source source = context.sources.get(context, importId);
          return inner.get(context, fromImportPath, importId, source);
        });
  }

  private Value lookupRef(ModuleCompileContext context, SemiserialRef ref) {
    return ref.dispatchRef(
        new SemiserialRef.Dispatcher<Value>() {
          @Override
          public Value handleArtifact(SemiserialRefArtifact s) {
            return context.artifactLookup.getOpt(s.id);
          }

          @Override
          public Value handleBuiltin(SemiserialRefBuiltin s) {
            return Builtin.semiKeyToBuiltin.get(s.key);
          }
        });
  }

  public CompletableFuture<Value> get(ModuleCompileContext context, ImportId importId) {
    return getModule(context.compileContext, context.importPath, importId)
        .thenApply(
            semi -> {
              TSList<ROPair<ArtifactId, SemiserialValue>> remaining =
                  new TSList<ROPair<ArtifactId, SemiserialValue>>();
              for (int i = 0; i < semi.result().artifacts.size(); i++) {
                final SemiserialValue semiValue = semi.result().artifacts.get(i);
                remaining.add(new ROPair<>(new ArtifactId(importId, i), semiValue));
              }
              // type, (id, semivalue)
              TSList<ROPair<Value, ROPair<ArtifactId, SemiserialValue>>> stratum = new TSList<>();
              do {
                stratum.clear();
                final Iterator<ROPair<ArtifactId, SemiserialValue>> iter = remaining.iterator();

                // Find next subset of artifacts whose types are all resolved
                while (iter.hasNext()) {
                  final ROPair<ArtifactId, SemiserialValue> pair = iter.next();
                  final SemiserialValue semiValue = pair.second;
                  Value type = lookupRef(context, semiValue.type);
                  if (type != null) {
                    stratum.add(new ROPair<>(type, pair));
                    iter.remove();
                  }
                }

                // Desemiserialize this stratum
                Desemiserializer typeDesemiserializer = new Desemiserializer();
                for (ROPair<Value, ROPair<ArtifactId, SemiserialValue>> candidate : stratum) {
                  final Value value =
                      candidate.first.graphDeserializeValue(
                          context, typeDesemiserializer, candidate.second.second.data);
                  context.artifactLookup.put(candidate.second.first, value);
                  context.backArtifactLookup.put(value, candidate.second.first);
                }
                typeDesemiserializer.finish();
              } while (stratum.some());
              if (remaining.some()) {
                /**
                 * Type dependency loop. Probbly needs user-facing error but this shouldn't ever
                 * happen - it should be cought during initial semiserialization.
                 */
                throw new Assertion();
              }
              final Value out = lookupRef(context, semi.result().root);
              if (out == null) {
                /** Shouldn't happen unless someone messes with the cache data directly. */
                throw new Assertion();
              }
              return out;
            });
  }
}
