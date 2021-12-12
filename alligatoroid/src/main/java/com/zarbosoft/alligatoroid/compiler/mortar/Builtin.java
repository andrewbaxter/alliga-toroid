package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMBuiltin;

/** Fields in top level builtin -- reflected into builtin value */
public class Builtin {
  public static final JVMBuiltin jvm = new JVMBuiltin();
  public static final Value _null = NullValue.value;
  public static final Value nullType = NullType.type;

  public static void log(Module module, String message) {
    module.log.log.add(message);
  }

  public static CreatedFile createFile(String path) {
    return new CreatedFile(path);
  }
}
