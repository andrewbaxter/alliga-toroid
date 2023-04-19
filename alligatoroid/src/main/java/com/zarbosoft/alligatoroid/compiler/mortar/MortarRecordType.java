package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class MortarRecordType implements MortarDataTypeForGeneric {
  public final ROList<ROPair<Object, MortarRecordField>> fields;
  public static final JavaDataDescriptor DESC =
      JavaDataDescriptor.fromObjectClass(Object.class).array();

  public MortarRecordType(ROList<ROPair<Object, MortarRecordField>> fields) {
    this.fields = fields;
  }

  @Override
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return new MortarDataGenericBindingVar(key, newTypestate());
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return DESC;
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

  public MortarRecordTypestate newTypestate() {
    final TSList<ROPair<Object, MortarRecordFieldstate>> outFields = new TSList<>();
    for (ROPair<Object, MortarRecordField> field : fields) {
      outFields.add(new ROPair<>(field.first, field.second.recordfield_newFieldstate()));
    }
    return new MortarRecordTypestate(outFields);
  }

  @Override
  public MortarDataTypestateForGeneric type_newTypestate() {
    return newTypestate();
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return new MortarDataGenericTupleField(offset, this);
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
    return new MortarDataGenericField(parentType, fieldName, this);
  }
}
