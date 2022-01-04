package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMConstructor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSSet;

public class JVMConcreteClassBuilder {
  public final TSSet<ROTuple> incompleteConstructors = new TSSet<>();
  public final TSSet<ROTuple> incompleteMethods = new TSSet<>();
  public final JVMSharedClass jvmClass;
  public final JVMClassType base;
  public byte[] built;

  public JVMConcreteClassBuilder(JVMClassType base) {
    this.base = base;
    this.jvmClass = new JVMSharedClass(JVMSharedJVMName.fromNormalName(base.jvmExternalClass));
  }

  @Meta.WrapExpose
  public RetConstructor constructor(Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    incompleteConstructors.add(specDetails.keyTuple);
    base.constructors.put(specDetails.keyTuple, specDetails);
    final JVMConstructor constructor = JVMConstructor.create(base, spec);
    return new RetConstructor(constructor, new JVMConcreteConstructorBuilder(this, constructor));
  }

  @Meta.WrapExpose
  public JVMConcreteMethodBuilder declareMethod(String name, Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    ROTuple keyTuple = ROTuple.create(name).append(specDetails.keyTuple);
    incompleteMethods.add(keyTuple);
    base.methodFields.put(keyTuple, new JVMMethodFieldType(base, name, spec));
    base.fields.add(name);
    return new JVMConcreteMethodBuilder(this, keyTuple, specDetails);
  }

  @Meta.WrapExpose
  public byte[] bytes() {
    build();
    return built;
  }

  public void build() {
    if (built != null) return;
    if (incompleteConstructors.some()) {
      throw new ConstructorsNotDefined(incompleteConstructors.ro());
    }
    if (incompleteMethods.some()) {
      throw new MethodsNotDefined(incompleteMethods.ro());
    }
    built = jvmClass.render();
  }

  public static class RetConstructor {
    public final JVMConstructor constructor;
    public final JVMConcreteConstructorBuilder builder;

    public RetConstructor(JVMConstructor constructor, JVMConcreteConstructorBuilder builder) {
      this.constructor = constructor;
      this.builder = builder;
    }
  }

  public static class ConstructorsNotDefined extends RuntimeException {
    public final ROSet<ROTuple> incompleteConstructors;

    public ConstructorsNotDefined(ROSet<ROTuple> incompleteConstructors) {
      this.incompleteConstructors = incompleteConstructors;
    }
  }

  public static class MethodsNotDefined extends RuntimeException {
    public final ROSet<ROTuple> incompleteMethods;

    public MethodsNotDefined(ROSet<ROTuple> incompleteMethods) {
      this.incompleteMethods = incompleteMethods;
    }
  }
}
