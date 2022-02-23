package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMBuiltin;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.CreatedFile;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.luxem.write.Writer;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

/** Fields in top level builtin -- reflected into builtin value */
@Meta.Aggregate
public class MortarBuiltin {
  public static final JVMBuiltin jvm = new JVMBuiltin();
  public static final Value _null = nullValue;
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

  public static ImportId modRemote(String url, String hash) {
    return ImportId.create(RemoteModuleId.create(url, hash));
  }

  public static ImportId modLocal(ModuleCompileContext modContext, String path) {
    return ImportId.create(modContext.importId.moduleId.relative(path));
  }
}
