package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMExternClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.rendaw.common.ROTuple;

public class JVMExternClassBuilder {
  public final JVMExternClassType base;

  public JVMExternClassBuilder(JVMExternClassType base) {
    this.base = base;
  }

  @Meta.WrapExpose
  public void inherit(JVMDataType type) {
    base.inherits.add((JVMClassType) type);
  }

  @Meta.WrapExpose
  public void constructor(Record spec) {
    JVMUtils.MethodSpecDetails specDetails =
        JVMUtils.methodSpecDetails(spec);
    base.constructors.put(specDetails.keyTuple, specDetails);
  }

  @Meta.WrapExpose
  public void method(String name, Record spec) {
    JVMUtils.MethodSpecDetails specDetails =
        JVMUtils.methodSpecDetails(spec);
    JVMMethodFieldType field =
        new JVMMethodFieldType(base,name, spec);
    if (specDetails.isStatic) {
      base.staticMethodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    } else {
      base.methodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    }
  }

  @Meta.WrapExpose
  public void data(String name, Record spec) {
    JVMUtils.DataSpecDetails specDetails =
        JVMUtils.dataSpecDetails(spec);
    if (specDetails.isStatic) {
      base.staticDataFields.putNew(name, specDetails.type);
      base.staticFields.add(name);
    } else {
      base.dataFields.putNew(name, specDetails.type);
      base.fields.add(name);
    }
  }
}
