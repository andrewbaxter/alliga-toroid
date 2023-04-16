package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class MortarObjectImplType implements MortarDataType {
  public final MortarObjectInnerType meta;
  public final ROMap<Object, MortarObjectField> fields;

  public MortarObjectImplType(MortarObjectInnerType meta, ROMap<Object, MortarObjectField> fields) {
    this.meta = meta;
    this.fields = fields;
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
  return meta.jvmDesc();
  }

  @Override
  public Value type_stackAsValue() {
    return new MortarDataValueVariableStack(newTypestate());
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public Value type_constAsValue(Object data) {
    return MortarDataValueConst.create(newTypestate(), data);
  }

  @Override
  public MortarDataTypestate type_newTypestate() {
  return newTypestate();
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
  return new MortarDataGenericField(parentType,fieldName,this);
  }

  public MortarObjectImplTypestateAll newTypestate() {
    TSMap<Object, MortarObjectFieldstate> fields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectField> field : this.fields) {
      fields.put(field.getKey(), field.getValue().field_newFieldstate());
    }
    return MortarObjectImplTypestateAll.create(meta, fields);
  }

  @Override
  public Binding type_newInitialBinding(
      JavaBytecodeBindingKey key, JavaBytecodeCatchKey finallyKey) {
    return new MortarDataVarBinding(key, newTypestate(), finallyKey);
  }
}
