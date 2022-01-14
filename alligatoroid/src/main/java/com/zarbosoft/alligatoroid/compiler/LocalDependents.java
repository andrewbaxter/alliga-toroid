package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.PrototypeAuto;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Logger;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class LocalDependents {
  private final ROSetRef<String> localDirty;
  private final List<ROPair<String, String>> localDependencies = new ArrayList<>();
  private final ConcurrentHashMap<String, String> localHashes = new ConcurrentHashMap<>();
  private Path cachePath;

  public LocalDependents(Logger logger, ModuleId rootModuleId, Path cacheRoot) {
    final PrototypeAuto valueProto = new PrototypeAuto(DependentState.class);
    localDirty =
        rootModuleId.dispatch(
            new ModuleId.Dispatcher<ROSetRef<String>>() {
              @Override
              public ROSetRef<String> handleLocal(LocalModuleId id) {
                cachePath = localHashesPath(id, cacheRoot);
                TSList<Error> errors = new TSList<>();
                final BaseStateRecordBody<Void, ROMap<String, DependentState>> rootState =
                    new BaseStateRecordBody<>() {
                      final TSList<ROPair<String, BaseStateSingle<Void, DependentState>>> entries =
                          new TSList<>();

                      @Override
                      public ROMap<String, DependentState> build(
                          Void context, TSList<Error> errors) {
                        TSMap<String, DependentState> out = new TSMap<>();
                        for (ROPair<String, BaseStateSingle<Void, DependentState>> entry :
                            entries) {
                          out.put(entry.first, entry.second.build(null, errors));
                        }
                        return out;
                      }

                      @Override
                      public BaseStateSingle createKeyState(
                          Void context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
                        return new StateString();
                      }

                      @Override
                      public BaseStateSingle createValueState(
                          Void context,
                          TSList<Error> errors,
                          LuxemPathBuilder luxemPath,
                          Object key) {
                        final BaseStateSingle out = valueProto.create(errors, luxemPath);
                        entries.add(new ROPair<>((String) key, out));
                        return out;
                      }
                    };
                Deserializer.deserialize(null, errors, cachePath, new TSList<>(rootState));
                ROMap<String, DependentState> dependents = rootState.build(null, errors);
                if (errors.some()) {
                  for (Error error : errors) {
                    logger.warn(error);
                  }
                  return ROSet.empty;
                }
                TSSet<String> dirty = new TSSet<>();
                new Object() {
                  {
                    for (Map.Entry<String, DependentState> source : dependents) {
                      String gotHash = null;
                      try {
                        gotHash = new Utils.SHA256().add(Paths.get(source.getKey())).buildHex();
                      } catch (Common.UncheckedFileNotFoundException ignored) {
                      }
                      if (gotHash == null || !gotHash.equals(source.getValue().hash))
                        markDirty(source.getKey());
                    }
                  }

                  private void markDirty(String path) {
                    dirty.add(path);
                    DependentState state = dependents.getOpt(path);
                    if (state != null)
                      for (String s : state.dependents) {
                        markDirty(s);
                      }
                  }
                };
                return dirty;
              }

              @Override
              public ROSetRef<String> handleRemote(RemoteModuleId id) {
                return ROSet.empty;
              }

              @Override
              public ROSetRef<String> handleBundle(BundleModuleSubId id) {
                return id.module.dispatch(this);
              }

              @Override
              public ROSetRef<String> handleRoot(RootModuleId id) {
                throw new Assertion();
              }
            });
  }

  private Path localHashesPath(LocalModuleId id, Path cacheRoot) {
    return Utils.uniqueDir(
        cacheRoot.resolve("local_hashes"), id.path.getBytes(StandardCharsets.UTF_8));
  }

  public boolean isDirty(Path sourcePath) {
    return localDirty.contains(sourcePath.toString());
  }

  public void addDependency(String source, String dependency) {
    synchronized (localDependencies) {
      localDependencies.add(new ROPair<>(source, dependency));
    }
  }

  public void addHash(String source, String hash) {
    localHashes.put(source, hash);
  }

  public void write() {
    if (cachePath == null) return;
    TSMap<String, TSSet<String>> dependents = new TSMap<>();
    for (ROPair<String, String> dependency : localDependencies) {
      dependents.getCreate(dependency.second, () -> new TSSet<>()).add(dependency.first);
    }
    uncheck(
        () -> {
          try (OutputStream os = Files.newOutputStream(cachePath)) {
            final Writer writer = new Writer(os, (byte) ' ', 4);
            writer.recordBegin();
            for (Map.Entry<String, TSSet<String>> e : dependents) {
              writer.recordBegin();
              writer.primitive("hash").primitive(localHashes.get(e.getKey()));
              writer.primitive("dependents").arrayBegin();
              for (String dependent : e.getValue()) {
                writer.primitive(dependent);
              }
              writer.arrayEnd();
              writer.recordEnd();
            }
            writer.recordEnd();
          }
        });
  }

  private static class DependentState {
    private final String hash;
    private final ROSet<String> dependents;

    private DependentState(String hash, ROSet<String> dependents) {
      this.hash = hash;
      this.dependents = dependents;
    }
  }
}
