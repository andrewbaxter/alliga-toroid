package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Objects;

public class JVMExternClassBuilder {
  public final JVMExternClassType base;

  public JVMExternClassBuilder(JVMExternClassType base) {
    this.base = base;
  }

  @Builtin.WrapExpose
  public void inherit(JVMDataType type) {
    base.inherits.add((JVMBaseClassType) type);
  }

  @Builtin.WrapExpose
  public RetConstructor constructor() {
    JVMExternConstructor constructor = new JVMExternConstructor(base, new TSList<>());
    return new RetConstructor(constructor, new JVMExternConstructorBuilder(constructor));
  }

  @Builtin.WrapExpose
  public void declareMethod(String name, Record spec) {
    boolean isStatic = Objects.equals(spec.data.getOpt("static"), true);
    if (isStatic) {
      base.preStaticMethodFields.getCreate(name, () -> new TSList<>()).add(spec);
      base.staticFields.add(name);
    } else {
      base.preMethodFields.getCreate(name, () -> new TSList<>()).add(spec);
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

  public static class RetConstructor {
    public final JVMExternConstructor constructor;
    public final JVMExternConstructorBuilder builder;

    public RetConstructor(JVMExternConstructor constructor, JVMExternConstructorBuilder builder) {
      this.constructor = constructor;
      this.builder = builder;
    }
  }
}
