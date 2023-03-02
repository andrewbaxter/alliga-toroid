package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.rendaw.common.TSList;

public interface MortarDataProtoType {
  JavaDataDescriptor jvmDesc();

  Value protoTypeStackAsValue(JavaBytecodeSequence code);

  boolean protoTypeAssertAssignableFrom(TSList<Error> errors, Location id, Value value);

  JavaBytecode protoTypeAssignFrom(Value value);

  JavaBytecode protoTypeReturnBytecode();

  MortarDataType protoTypeNewType();

  MortarFieldProtoType protoTypeNewProtoField(ObjectMeta baseMeta, String name);

  default Value protoTypeConstAsValue(Object data) {
    return ConstDataValue.create(protoTypeNewType(), data);
  }
}
