package com.zarbosoft.alligatoroid.compiler.mortar.value.whole;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.jvm.value.direct.JVMBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.CreatedFile;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.NullType;
import com.zarbosoft.luxem.write.Writer;

/** Fields in top level builtin -- reflected into builtin value */
public class MortarBuiltin {
  public static final JVMBuiltin jvm = new JVMBuiltin();
  public static final Value _null = NullValue.value;
  public static final NullType nullType = NullType.type;

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
