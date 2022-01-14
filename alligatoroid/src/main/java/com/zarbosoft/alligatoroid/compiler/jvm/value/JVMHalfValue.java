package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.OkValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class JVMHalfValue implements OkValue, NoExportValue, LeafValue {
  public final JVMProtocode lower;
  private final JVMHalfDataType type;

  public JVMHalfValue(JVMHalfDataType type, JVMProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public Value type() {
    throw new Assertion();
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.valueBind(lower.lower(context));
  }
}
