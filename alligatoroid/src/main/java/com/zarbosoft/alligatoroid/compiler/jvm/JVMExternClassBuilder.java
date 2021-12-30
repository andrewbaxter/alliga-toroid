package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.ROTuple;

public class JVMExternClassBuilder {
  public final JVMExternClassType base;

  public JVMExternClassBuilder(JVMExternClassType base) {
    this.base = base;
  }

  @com.zarbosoft.alligatoroid.compiler.Builtin.WrapExpose
  public void inherit(JVMDataType type) {
    base.inherits.add((JVMBaseClassType) type);
  }

  @com.zarbosoft.alligatoroid.compiler.Builtin.WrapExpose
  public void constructor(Record spec) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.methodSpecDetails(spec);
    base.constructorSigs.put(specDetails.keyTuple, specDetails);
  }

  @com.zarbosoft.alligatoroid.compiler.Builtin.WrapExpose
  public void method(String name, Record spec) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.methodSpecDetails(spec);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = base;
    if (specDetails.isStatic) {
      base.staticMethodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    } else {
      base.methodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    }
  }

  @Builtin.WrapExpose
  public void data(String name, Record spec) {
    JVMShallowMethodFieldType.DataSpecDetails specDetails =
        JVMShallowMethodFieldType.dataSpecDetails(spec);
    if (specDetails.isStatic) {
      base.staticDataFields.putNew(name, specDetails.type);
      base.staticFields.add(name);
    } else {
      base.dataFields.putNew(name, specDetails.type);
      base.fields.add(name);
    }
  }
}
