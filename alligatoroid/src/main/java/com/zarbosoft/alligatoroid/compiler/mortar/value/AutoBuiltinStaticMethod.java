package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfDataType;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgument;

public class AutoBuiltinStaticMethod implements SimpleValue, AutoBuiltinExportable, LeafExportable {
  private final String name;
  private final JVMSharedFuncDescriptor desc;
  private final JVMSharedJVMName base;
  private final MortarHalfDataType returnType;

  public AutoBuiltinStaticMethod(
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
