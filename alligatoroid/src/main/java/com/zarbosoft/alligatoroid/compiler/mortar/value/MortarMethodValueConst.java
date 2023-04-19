package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;

public class MortarMethodValueConst implements Value {
  private final StaticAutogen.FuncInfo funcInfo;
  private final Object base;

  public MortarMethodValueConst(StaticAutogen.FuncInfo funcInfo, Object base) {
    this.funcInfo = funcInfo;
    this.base = base;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    return MortarMethodValueVariableDeferred.call(
        context,
        location,
        ((MortarTargetModuleContext) context.target).transfer(base),
        funcInfo,
        argument);
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    context.errors.add(new GeneralLocationError(id, "Methods can't be the results of branches"));
    return EvaluateResult.error;
  }

  @Override
  public AlligatorusType type(EvaluationContext context) {
  return NullType.type;
  }
}
