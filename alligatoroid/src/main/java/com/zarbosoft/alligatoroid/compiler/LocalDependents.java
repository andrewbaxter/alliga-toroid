package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.AutoTreeMeta;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Logger;
import com.zarbosoft.luxem.read.path.LuxemArrayPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
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
  private static final AutoTreeMeta serialMeta;

  static {
    serialMeta = new AutoTreeMeta();
    serialMeta.scan(SerialFormat.class);
  }

  private final ROSetRef<String> localDirty;
  private final List<ROPair<String, String>> localDependencies = new ArrayList<>();
  private final ConcurrentHashMap<String, String> localHashes = new ConcurrentHashMap<>();
  private Path cachePath;

  public LocalDependents(Logger logger, ModuleId rootModuleId, Path cacheRoot) {
    localDirty =
        rootModuleId.dispatch(
            new ModuleId.Dispatcher<ROSetRef<String>>() {
              @Override
              public ROSetRef<String> handleLocal(LocalModuleId id) {
                cachePath =
                    Utils.uniqueDir(
                            cacheRoot.resolve("local_hashes"),
                            id.path.getBytes(StandardCharsets.UTF_8))
                        .resolve("dependents");
                TSList<Error> errors = new TSList<>();
                final BaseStateSingle<Object, SerialFormat> rootState =
                    serialMeta.deserialize(
                        errors, new LuxemArrayPathBuilder(null), SerialFormat.class);
                try {
                  Deserializer.deserialize(null, errors, cachePath, new TSList<>(rootState));
                } catch (Common.UncheckedFileNotFoundException ignored) {
                  return ROSet.empty;
                }
                SerialFormat dependents = rootState.build(null, errors);
                if (errors.some()) {
                  for (Error error : errors) {
                    logger.warn(error);
                  }
                  return ROSet.empty;
                }
                TSSet<String> dirty = new TSSet<>();
                new Object() {
                  {
                    for (Map.Entry<String, SerialFormatElement> source : dependents.elements) {
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
                    SerialFormatElement state = dependents.elements.getOpt(path);
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

  public boolean isDirty(String sourcePath) {
    return localDirty.contains(sourcePath);
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
    TSMap<String, SerialFormatElement> data = new TSMap<>();
    for (ROPair<String, String> dependency : localDependencies) {
      data.getCreate(
              dependency.second,
              () -> new SerialFormatElement(localHashes.get(dependency.second), new TSSet<>()))
          .dependents
          .add(dependency.first);
    }
    uncheck(
        () -> {
          try (OutputStream os = Files.newOutputStream(cachePath)) {
            final Writer writer = new Writer(os, (byte) ' ', 4);
            serialMeta.serialize(
                writer, TypeInfo.fromClass(SerialFormat.class), new SerialFormat(data));
          }
        });
  }

  public static class SerialFormatElement {
    public final String hash;
    public final TSSet<String> dependents;

    public SerialFormatElement(String hash, TSSet<String> dependents) {
      this.hash = hash;
      this.dependents = dependents;
    }
  }

  public static class SerialFormat {
    public final TSMap<String, SerialFormatElement> elements;

    public SerialFormat(TSMap<String, SerialFormatElement> elements) {
      this.elements = elements;
    }
  }
}
