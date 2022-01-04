package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgument;

public class AutoBuiltinFunctionType implements SimpleValue, AutoGraphMixin, LeafValue {
  private final String name;
  private final JVMSharedFuncDescriptor desc;
  private final JVMSharedJVMName base;
  private final MortarHalfDataType returnType;

  public AutoBuiltinFunctionType(
      JVMSharedJVMName base,
      String name,
      JVMSharedFuncDescriptor desc,
      MortarHalfDataType returnType) {
    this.name = name;
    this.desc = desc;
    this.base = base;
    this.returnType = returnType;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    JVMSharedCode code = new JVMSharedCode();
    convertFunctionArgument(context, code, argument);
    code.add(JVMSharedCode.callStaticMethod(context.sourceLocation(location), base, name, desc));
    if (returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(returnType.stackAsValue(code));
  }
}
