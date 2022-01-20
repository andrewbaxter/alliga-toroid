package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfDataType;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarHalfValue implements OkValue, NoExportValue, Exportable {
  public final MortarProtocode lower;
  public final MortarHalfDataType type;

  public MortarHalfValue(MortarHalfDataType type, MortarProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    return lower.mortarDrop(context, location);
  }

  @Override
  public EvaluateResult mortarAccess(EvaluationContext context, Location location, MortarValue field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, MortarBinding> mortarBind(EvaluationContext context, Location location) {
    return type.valueBind(context, lower);
  }

  public JVMSharedCodeElement mortarLower(EvaluationContext context) {
    return lower.mortarHalfLower(context);
  }
}
