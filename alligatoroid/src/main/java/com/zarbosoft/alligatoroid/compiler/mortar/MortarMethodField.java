package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import org.objectweb.asm.tree.MethodInsnNode;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class MortarMethodField implements SimpleValue {
  private final MortarProtocode lower;
  private final MortarMethodFieldType type;

  public MortarMethodField(MortarProtocode lower, MortarMethodFieldType type) {
    this.lower = lower;
    this.type = type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    MortarCode code = (MortarCode) new MortarCode().add(lower.lower());
    if (type.needsModule)
      code.add(((MortarTargetModuleContext) context.target).transfer(context.moduleContext));
    MortarTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.line(context.moduleContext.sourceLocation(location))
        .add(new MethodInsnNode(INVOKEVIRTUAL, type.base.jvmName, type.name, type.jbcDesc, false));
    if (type.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(type.returnType.stackAsValue(code));
  }
}
