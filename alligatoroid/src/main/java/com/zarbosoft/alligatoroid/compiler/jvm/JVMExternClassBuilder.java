package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.ROTuple;

public class JVMExternClassBuilder {
  public final JVMBaseClassType base;

  public JVMExternClassBuilder(JVMBaseClassType base) {
    this.base = base;
  }

  @Builtin.WrapExpose
  public void inherit(JVMDataType type) {
    base.inherits.add((JVMBaseClassType) type);
  }

  @Builtin.WrapExpose
  public void declareMethod(String name, Record spec) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.methodSpecDetails(spec);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = base;
    if (specDetails.isStatic) {
      base.staticMethodFields.putNew(ROTuple.create(name).append(specDetails.keyTuple), field);
      base.staticFields.add(name);
    } else {
      base.methodFields.putNew(ROTuple.create(name).append(specDetails.keyTuple), field);
      base.fields.add(name);
    }
  }

  @Builtin.WrapExpose
  public void declareData(String name, Record spec) {
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
