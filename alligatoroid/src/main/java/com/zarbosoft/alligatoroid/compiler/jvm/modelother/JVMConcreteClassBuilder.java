package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.mortar.ContinueError;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSSet;

public class JVMConcreteClassBuilder {
  public final TSSet<ROTuple> incompleteConstructors = new TSSet<>();
  public final TSSet<ROTuple> incompleteMethods = new TSSet<>();
  public final JVMSharedClass jvmClass;
  public final JVMClassInstanceType base;
  public byte[] built;
  public boolean error;

  public JVMConcreteClassBuilder(JVMClassInstanceType base) {
    this.base = base;
    this.jvmClass = new JVMSharedClass(JVMSharedJVMName.fromNormalName(base.name));
  }

  @Meta.WrapExpose
  public RetConstructor constructor(Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    incompleteConstructors.add(specDetails.argTuple);
    base.constructors.put(specDetails.argTuple, specDetails);
    final JVMConstructor constructor = new JVMConstructor(base, specDetails);
    return new RetConstructor(constructor, new JVMConcreteConstructorBuilder(this, constructor));
  }

  @Meta.WrapExpose
  public JVMConcreteMethodBuilder declareMethod(String name, Record spec) {
    JVMUtils.MethodSpecDetails specDetails = JVMUtils.methodSpecDetails(spec);
    ROTuple keyTuple = ROTuple.create(name).append(specDetails.argTuple);
    incompleteMethods.add(keyTuple);
    base.ensureField(name).methods.add(specDetails);
    return new JVMConcreteMethodBuilder(this, keyTuple, specDetails);
  }

  @Meta.WrapExpose
  public byte[] bytes() {
    build();
    return built;
  }

  public void build() {
    if (error) throw new ContinueError();
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
