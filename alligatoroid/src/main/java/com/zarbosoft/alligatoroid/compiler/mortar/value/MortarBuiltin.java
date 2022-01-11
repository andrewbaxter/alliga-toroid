package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.CreatedFile;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfNullType;
import com.zarbosoft.luxem.write.Writer;

/** Fields in top level builtin -- reflected into builtin value */
public class MortarBuiltin {
  public static final JVMBuiltin jvm = new JVMBuiltin();
  public static final Value _null = NullValue.value;
  public static final MortarHalfNullType nullType = MortarHalfNullType.type;

  public static void log(ModuleCompileContext module, String message) {
    module.compileContext.logger.info(
        new TreeSerializable() {
          @Override
          public void treeSerialize(Writer writer) {
            writer.primitive(message);
          }
        });
  }

  public static CreatedFile createFile(String path) {
    return new CreatedFile(path);
  }
}
