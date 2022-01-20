package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class JVMHalfValue implements NoExportValue, Exportable, JVMOkValue {
  public final JVMProtocode lower;
  private final JVMHalfDataType type;

  public JVMHalfValue(JVMHalfDataType type, JVMProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode jvmDrop(EvaluationContext context, Location location) {
    return lower.jvmDrop(context, location);
  }

  @Override
  public MortarValue type() {
    throw new Assertion();
  }

  @Override
  public EvaluateResult jvmAccess(EvaluationContext context, Location location, MortarValue field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, ? extends Binding> jvmBind(
      EvaluationContext context, Location location) {
    return type.valueBind(lower.jvmLower(context));
  }

  public JVMSharedCodeElement jvmLower(EvaluationContext context) {
    return lower.jvmLower(context);
  }
}
