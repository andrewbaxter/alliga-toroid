package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import org.objectweb.asm.tree.MethodInsnNode;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgument;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class AutoBuiltinFunctionType implements SimpleValue, AutoGraphMixin, LeafValue {
  private final String name;
  private final String jbcDesc;
  private final String jbcInternalClass;
  private final MortarHalfDataType returnType;

  public AutoBuiltinFunctionType(
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
    code.line(context.sourceLocation(location))
        .add(new MethodInsnNode(INVOKESTATIC, jbcInternalClass, name, jbcDesc, false));
    if (returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(returnType.stackAsValue(code));
  }
}
