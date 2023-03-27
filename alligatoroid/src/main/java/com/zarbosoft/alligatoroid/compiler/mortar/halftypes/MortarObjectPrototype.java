package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataVariableValue;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class MortarObjectPrototype implements MortarDataPrototype {
  public final ObjectMeta meta;
  public final ROMap<Object, MortarProtofield> fields;

  public MortarObjectPrototype(ObjectMeta meta, ROMap<Object, MortarProtofield> fields) {
    this.meta = meta;
    this.fields = fields;
  }

  public MortarObjectPrototype() {
    meta = null;
    fields = null;
  }

  @Override
  public JavaDataDescriptor prototype_jvmDesc() {
    return JavaDataDescriptor.fromJVMName(meta.name.asInternalName());
  }

  @Override
  public Value prototype_stackAsValue(JavaBytecode code) {
    return new MortarDataVariableValue(prototype_newType(), new MortarDeferredCodeStack(code));
  }

  @Override
  public JavaBytecode prototype_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public MortarDataType prototype_newType() {
    TSList<ObjectMeta> implements_ = new TSList<>();
    for (ObjectMeta m : meta.implements_) {
      implements_.add(m);
    }
    TSMap<Object, Field> fields = new TSMap<>();
    for (Map.Entry<Object, MortarProtofield> field : this.fields) {
      fields.put(field.getKey(), field.getValue().protofield_newField());
    }
    return MortarObjectType.create(meta, fields);
  }

  @Override
  public MortarProtofield prototype_newProtofield(ObjectMeta baseMeta, String name) {
    return new MortarDataProtofield(this, baseMeta, name);
  }
}
