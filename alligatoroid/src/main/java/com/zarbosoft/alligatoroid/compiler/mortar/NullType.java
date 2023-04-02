package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.rendaw.common.Assertion;

public class NullType implements MortarDataType {
  public static final NullType type = new NullType();

  private NullType() {}

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  @Override
  public Value type_stackAsValue(JavaBytecode code) {
    // TODO gracefully fail
    throw new Assertion();
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return returnBytecode();
  }

  @Override
  public Value type_constAsValue(Object data) {
    return NullValue.value;
  }

  public static JavaBytecode returnBytecode() {
    return JavaBytecodeUtils.returnVoid;
  }

  public static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.VOID;
  }

  @Override
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return NullBinding.binding;
  }
}
