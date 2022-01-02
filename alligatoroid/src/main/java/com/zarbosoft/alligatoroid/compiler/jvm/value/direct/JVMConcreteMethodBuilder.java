package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.RETURN;

public class JVMConcreteMethodBuilder {
  private final JVMConcreteClassBuilder classBuilder;
  private final ROTuple key;
  private final JVMUtils.MethodSpecDetails specDetails;
  private JVMCode built;

  public JVMConcreteMethodBuilder(
      JVMConcreteClassBuilder classBuilder,
      ROTuple keyTuple,
      JVMUtils.MethodSpecDetails specDetails) {
    this.classBuilder = classBuilder;
    this.key = keyTuple;
    this.specDetails = specDetails;
  }

  @Meta.WrapExpose
  public void implement(ModuleCompileContext module, Value body) {
    String name = (String) key.get(0);
    JVMMethodFieldType field =
        new JVMMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = classBuilder.base;
    JVMTargetModuleContext targetContext = new JVMTargetModuleContext();
    EvaluationContext context = new EvaluationContext(module, targetContext, new Scope(null));

    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    ectx.record(body.evaluate(context));
    built = (JVMCode) ectx.build(null).preEffect;
    classBuilder.jvmClass.defineFunction(
        name, specDetails.jvmSigDesc, new JVMCode().add(built).add(RETURN), new TSList<>());
    classBuilder.incompleteMethods.remove(key);
  }
}
