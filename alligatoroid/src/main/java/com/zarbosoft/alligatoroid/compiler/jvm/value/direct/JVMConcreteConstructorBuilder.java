package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMConstructor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.RETURN;

public class JVMConcreteConstructorBuilder {
  private final JVMConcreteClassBuilder classBuilder;
  private final JVMConstructor constructor;
  private JVMSharedCode built;

  public JVMConcreteConstructorBuilder(
      JVMConcreteClassBuilder classBuilder, JVMConstructor constructor) {
    this.classBuilder = classBuilder;
    this.constructor = constructor;
  }

  @Meta.WrapExpose
  public void implement(ModuleCompileContext module, Value body) {
    JVMTargetModuleContext targetContext = new JVMTargetModuleContext();
    EvaluationContext context = new EvaluationContext(module, targetContext, new Scope(null));
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    ectx.record(body.evaluate(context));
    built = (JVMSharedCode) ectx.build(null).preEffect;
    classBuilder.jvmClass.defineConstructor(
        constructor.specDetails.jvmSigDesc,
        new JVMSharedCode().add(built).addI(RETURN),
        new TSList<>());
    classBuilder.incompleteMethods.remove(constructor.specDetails.keyTuple);
  }
}
