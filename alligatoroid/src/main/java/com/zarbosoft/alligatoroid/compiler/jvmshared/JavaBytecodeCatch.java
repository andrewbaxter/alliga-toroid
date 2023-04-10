package com.zarbosoft.alligatoroid.compiler.jvmshared;

public class JavaBytecodeCatch implements JavaBytecode {
  public final JavaBytecodeCatchKey key;
  public final JavaBytecode inner;

  public JavaBytecodeCatch(JavaBytecodeCatchKey key, JavaBytecode inner) {
    this.key = key;
    this.inner = inner;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
  return dispatcher.handleCatch(this);
  }
}
