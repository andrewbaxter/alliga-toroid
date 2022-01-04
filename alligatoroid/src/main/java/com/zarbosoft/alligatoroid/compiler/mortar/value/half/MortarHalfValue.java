package com.zarbosoft.alligatoroid.compiler.mortar.value.half;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.NoExportValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.OkValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarHalfValue implements OkValue, LeafValue, NoExportValue {
  public final MortarProtocode lower;
  public final MortarHalfDataType type;

  public MortarHalfValue(MortarHalfDataType type, MortarProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.valueBind(context, lower);
  }

  public JVMSharedCodeElement lower(EvaluationContext context) {
    return lower.lower(context);
  }
}
