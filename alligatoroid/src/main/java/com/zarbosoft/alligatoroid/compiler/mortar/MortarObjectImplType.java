package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class MortarObjectImplType implements MortarDataTypeForGeneric {
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
    return Global.JBC_returnObj;
  }

  @Override
  public Value type_constAsValue(Object data) {
    return new MortarDataValueConst(newTypestate(), data);
  }

  @Override
  public MortarDataTypestateForGeneric type_newTypestate() {
    return newTypestate();
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
    return new MortarDataGenericField(parentType, fieldName, this);
  }

  public MortarImplTypestateAll newTypestate() {
    TSMap<Object, MortarObjectFieldstate> fields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectField> field : this.fields) {
      fields.put(field.getKey(), field.getValue().field_newFieldstate());
    }
    return MortarImplTypestateAll.create(meta, fields);
  }

  @Override
  public ROPair<JavaBytecodeBindingKey, Binding> type_newInitialBinding() {
    final JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    return new ROPair<>(key, new MortarDataGenericBindingVar(key, newTypestate()));
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return new MortarDataGenericRecordField(offset, this);
  }
}
