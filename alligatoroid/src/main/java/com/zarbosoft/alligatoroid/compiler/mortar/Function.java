package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import org.objectweb.asm.tree.MethodInsnNode;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgument;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class Function implements SimpleValue {
  private final String name;
  private final String jbcDesc;
  private final String jbcInternalClass;
  private final MortarHalfDataType returnType;

  public Function(
      String jbcInternalClass, String name, String jbcDesc, MortarHalfDataType returnType) {
    this.name = name;
    this.jbcDesc = jbcDesc;
    this.jbcInternalClass = jbcInternalClass;
    this.returnType = returnType;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    MortarCode code = new MortarCode();
    convertFunctionArgument(context, code, argument);
    code.line(context.moduleContext.sourceLocation(location))
        .add(new MethodInsnNode(INVOKESTATIC, jbcInternalClass, name, jbcDesc, false));
    if (returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(returnType.stackAsValue(code));
  }
}
