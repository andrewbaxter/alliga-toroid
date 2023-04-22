package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VoidValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
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

public final class BundleType extends VoidTypeSimple implements AutoExportable {
  @AutoExporter.Param public String root;
  @AutoExporter.Param public ImportId id;

  public static BundleType create(ImportId id, String root) {
    final BundleType out = new BundleType();
    out.id = id;
    out.root = root;
    out.postInit();
    return out;
  }

  @Override
  public ROList<String> typestate_traceFields(EvaluationContext context, Location location) {
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
  public EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field) {
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

  @Override
  public boolean typestate_canCastTo(AlligatorusType other) {
    return sameBundle(other);
  }

  private boolean sameBundle(AlligatorusType other) {
    if (other.getClass() != getClass()) {
      return false;
    }
    final BundleType other1 = (BundleType) other;
    return root.equals(other1.root) && id.equal1(other1.id);
  }

  @Override
  public EvaluateResult typestate_castTo(
      EvaluationContext context, Location location, VoidType other) {
    return EvaluateResult.pure(new VoidValue(this));
  }

  @Override
  public VoidTypestate typestate_unfork(
      EvaluationContext context, Location location, VoidTypestate other, Location otherLocation) {
    if (other.getClass() != getClass() || !sameBundle((AlligatorusType) other)) {
      context.errors.add(new GeneralLocationError(location, "Unfork type mismatch"));
      return null;
    }
    return this;
  }

  @Override
  public boolean recordfieldstate_canCastTo(AlligatorusType other) {
    return sameBundle(other);
  }

  @Override
  public boolean recordfieldstate_triviallyAssignableTo(MortarRecordFieldstate other) {
    return sameBundle(other.recordfieldstate_asType());
  }

  @Override
  public boolean recordfieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    return true;
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    if (other.getClass() != getClass() || !sameBundle((AlligatorusType) other)) {
      context.errors.add(new GeneralLocationError(location, "Unfork type mismatch"));
      return null;
    }
    return this;
  }

}
