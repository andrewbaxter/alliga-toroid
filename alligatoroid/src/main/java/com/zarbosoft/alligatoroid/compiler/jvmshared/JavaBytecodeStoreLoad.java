package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.rendaw.common.Assertion;

public class JavaBytecodeStoreLoad implements JavaBytecode, BuiltinAutoExportable {
  public int code;
  public JavaBytecodeBindingKey key;

  public static JavaBytecodeStoreLoad create(int code, JavaBytecodeBindingKey key) {
    final JavaBytecodeStoreLoad out = new JavaBytecodeStoreLoad();
    if (key == null) {
      throw new Assertion();
    }
    out.code = code;
    out.key = key;
    out.postInit();
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleStoreLoad(this);
  }
}
