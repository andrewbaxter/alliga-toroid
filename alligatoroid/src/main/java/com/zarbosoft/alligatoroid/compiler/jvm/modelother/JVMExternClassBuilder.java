package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMExternClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

public class JVMExternClassBuilder {
  public final JVMExternClassInstanceType base;

  public JVMExternClassBuilder(JVMExternClassInstanceType base) {
    this.base = base;
  }

  @Meta.WrapExpose
  public void inherit(JVMClassInstanceType type) {
    base.inherits.add(type);
  }

  @Meta.WrapExpose
  public void constructor(Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    base.constructors.put(specDetails.argTuple, specDetails);
  }

  @Meta.WrapExpose
  public void method(String name, Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    if (specDetails.isStatic) {
      base.staticFields
          .getCreate(name, () -> JVMPseudoFieldMeta.blank(base, name))
          .methods
          .add(specDetails);
    } else {
      base.fields
          .getCreate(name, () -> JVMPseudoFieldMeta.blank(base, name))
          .methods
          .add(specDetails);
    }
  }

  @Meta.WrapExpose
  public void data(String name, Record spec) {
    JVMUtils.DataSpecDetails specDetails = JVMUtils.dataSpecDetails(spec);
    if (specDetails.isStatic) {
      base.staticFields.getCreate(name, () -> JVMPseudoFieldMeta.blank(base, name)).data =
          specDetails;
    } else {
      base.fields.getCreate(name, () -> JVMPseudoFieldMeta.blank(base, name)).data = specDetails;
    }
  }
}
