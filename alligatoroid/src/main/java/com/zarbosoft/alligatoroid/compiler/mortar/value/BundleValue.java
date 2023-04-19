package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstString;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class BundleValue implements Value, BuiltinAutoExportable {
  @BuiltinAutoExportableType.Param public String root;
  @BuiltinAutoExportableType.Param public ImportId id;

  public static BundleValue create(ImportId id, String root) {
    final BundleValue out = new BundleValue();
    out.id = id;
    out.root = root;
    out.postInit();
    return out;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final Source source =
        context.moduleContext.compileContext.sources.get(
            context.moduleContext.compileContext, this.id.moduleId);
    TSList<String> out = new TSList<String>();
    String path =
        this.id.moduleId.dispatch(
            new ModuleId.Dispatcher<String>() {
              @Override
              public String handleLocal(LocalModuleId id) {
                return "";
              }

              @Override
              public String handleRemote(RemoteModuleId id) {
                return "";
              }

              @Override
              public String handleBundle(BundleModuleSubId id) {
                return id.path;
              }

              @Override
              public String handleRoot(RootModuleId id) {
                return "";
              }
            });
    final URI zipUri = URI.create("jar:file:" + source.path.toString());
    uncheck(
        () -> {
          Consumer<FileSystem> inner =
              fs -> {
                try (Stream<Path> st = uncheck(() -> Files.list(fs.getPath(path)))) {
                  st.forEach(
                      s -> {
                        out.add(s.getFileName().toString());
                      });
                }
              };
          try (FileSystem fs = FileSystems.newFileSystem(zipUri, Collections.emptyMap())) {
            inner.accept(fs);
          } catch (FileSystemAlreadyExistsException e) {
            try (FileSystem fs = FileSystems.getFileSystem(zipUri)) {
              inner.accept(fs);
            }
          }
        });
    return out;
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    context.errors.add(
        new GeneralLocationError(id, "Bundle imports can't be returned from branches"));
    return EvaluateResult.error;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final String key = assertConstString(context, location, field);
    if (key == null) {
      return EvaluateResult.error;
    }
    CompletableFuture<Value> importResult =
        context.moduleContext.getModule(
            context.moduleContext.compileContext.modules.getCacheId(
                ImportId.create(
                    BundleModuleSubId.create(
                        id.moduleId, Paths.get(root).resolve(key).toString()))));
    return EvaluateResult.pure(new FutureValue(importResult));
  }
}
