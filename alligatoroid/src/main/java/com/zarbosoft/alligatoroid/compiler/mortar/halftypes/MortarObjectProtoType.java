package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class MortarObjectProtoType implements MortarDataProtoType {
  public final ObjectMeta meta;
  public final ROMap<Object, MortarFieldProtoType> fields;

  public MortarObjectProtoType(ObjectMeta meta, ROMap<Object, MortarFieldProtoType> fields) {
    this.meta = meta;
    this.fields = fields;
  }

  public MortarObjectProtoType() {
    meta = null;
    fields = null;
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.fromJVMName(meta.name.asInternalName());
  }

  @Override
  public Value protoTypeStackAsValue(JavaBytecodeSequence code) {
    return new VariableDataValue(protoTypeNewType(), new MortarDeferredCodeStack(code));
  }

  @Override
  public boolean protoTypeAssertAssignableFrom(TSList<Error> errors, Location id, Value value) {
    return meta.assertAssignableFrom(errors, id, value);
  }

  @Override
  public JavaBytecode protoTypeAssignFrom(Value value) {
    return meta.assignFrom(errors, id, value);
  }

  @Override
  public JavaBytecode protoTypeReturnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public MortarDataType protoTypeNewType() {
    TSList<ObjectMeta> implements_ = new TSList<>();
    for (ObjectMeta m : meta.implements_) {
      implements_.add(m);
    }
    TSMap<Object, Field> fields = new TSMap<>();
    for (Map.Entry<Object, MortarFieldProtoType> field : this.fields) {
      fields.put(field.getKey(), field.getValue().protoTypeNewField());
    }
    return MortarObjectType.create(meta, fields);
  }

  @Override
  public MortarFieldProtoType protoTypeNewProtoField(ObjectMeta baseMeta, String name) {
    return new MortarDataFieldProtoType(this, baseMeta, name);
  }
}
