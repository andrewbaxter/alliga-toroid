package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

public class NullType implements MortarDataType {
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
    return JavaBytecodeUtils.returnVoid;
  }

  public static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.VOID;
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
