package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfExternClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.rendaw.common.TSList;

public class JVMExternClassBuilder {
  public final JVMHalfExternClassType base;

  public JVMExternClassBuilder(JVMHalfExternClassType base) {
    this.base = base;
  }

  @Meta.WrapExpose
  public void inherit(JVMHalfDataType type) {
    base.inherits.add((JVMHalfClassType) type);
  }

  @Meta.WrapExpose
  public void constructor(Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    base.constructors.put(specDetails.argTuple, specDetails);
  }

  @Meta.WrapExpose
  public void method(String name, Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    JVMMethodFieldType field = JVMMethodFieldType.create(base, name, spec);
    if (specDetails.isStatic) {
      base.staticMethodFields.getCreate(name, () -> new TSList<>()).add(field);
      base.staticFields.add(name);
    } else {
      base.methodFields.getCreate(name, () -> new TSList<>()).add(field);
      base.fields.add(name);
    }
  }

  @Meta.WrapExpose
  public void data(String name, Record spec) {
    JVMUtils.DataSpecDetails specDetails = JVMUtils.dataSpecDetails(spec);
    if (specDetails.isStatic) {
      base.staticDataFields.putNew(name, specDetails.type);
      base.staticFields.add(name);
    } else {
      base.dataFields.putNew(name, specDetails.type);
      base.fields.add(name);
    }
  }
}
