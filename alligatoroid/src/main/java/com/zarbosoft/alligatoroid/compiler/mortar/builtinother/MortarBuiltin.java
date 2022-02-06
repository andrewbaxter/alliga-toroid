package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMBuiltin;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportOutsideOwningBundleModule;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Fields in top level builtin -- reflected into builtin value */
@Builtin.Aggregate
public class MortarBuiltin {
  public static final JVMBuiltin jvm = new JVMBuiltin();
  public static final VariableDataStackValue _null = ConstNull.value;
  public static final MortarNullType nullType = MortarNullType.type;

  public static void log(ModuleCompileContext module, String message) {
    module.compileContext.logger.info(
        new TreeDumpable() {
          @Override
          public void treeDump(Writer writer) {
            writer.primitive(message);
          }
        });
  }

  public static CreatedFile createFile(String path) {
    return new CreatedFile(path);
  }

  public static RemoteModuleId modRemote(String url, String hash) {
    return new RemoteModuleId(url, hash);
  }

  public static ModuleId modLocal(ModuleCompileContext modContext, String path) {
    return modContext.importId.moduleId.dispatch(
        new ModuleId.Dispatcher<ModuleId>() {

          @Override
          public ModuleId handleLocal(LocalModuleId id) {
            return new LocalModuleId(
                Paths.get(id.path).resolveSibling(path).normalize().toString());
          }

          @Override
          public ModuleId handleRemote(RemoteModuleId id) {
            Path subpath = Paths.get(path).normalize();
            if (subpath.startsWith("..")) {
              throw new ImportOutsideOwningBundleModule(subpath.toString(), id);
            }
            return new BundleModuleSubId(id, subpath.toString());
          }

          @Override
          public ModuleId handleBundle(BundleModuleSubId id) {
            Path subpath = Paths.get(id.path).resolveSibling(path).normalize();
            if (subpath.startsWith("..")) {
              throw new ImportOutsideOwningBundleModule(subpath.toString(), id.module);
            }
            return new BundleModuleSubId(id.module, subpath.toString());
          }

          @Override
          public ModuleId handleRoot(RootModuleId id) {
            throw new Assertion();
          }
        });
  }
}
