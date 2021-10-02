package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;

public class JVMExternConstructorBuilder {
  public final JVMExternConstructor constructor;

  public JVMExternConstructorBuilder(JVMExternConstructor constructor) {
    this.constructor = constructor;
  }

  @Builtin.WrapExpose
  public void declare(Module module, Record spec) {
    constructor.preSigs.add(spec);
  }
}
