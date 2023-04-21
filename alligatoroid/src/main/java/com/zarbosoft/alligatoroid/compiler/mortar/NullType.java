package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;

public class NullType implements MortarDataType, BuiltinAutoExportable {
  public static final NullType type = new NullType();

  private NullType() {}

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  @Override
  public Value type_stackAsValue() {
    return NullValue.value;
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return returnBytecode();
  }

  @Override
  public Value type_constAsValue(Object data) {
    return NullValue.value;
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
    return NullFieldAll.inst;
  }

  public static JavaBytecode returnBytecode() {
    return Global.JBC_returnVoid;
  }

  public static JavaDataDescriptor jvmDesc() {
    return Global.DESC_VOID;
  }

  @Override
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return NullBinding.binding;
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return NullFieldAll.inst;
  }
}
