package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.MortarInvalidCast;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarType extends AlligatorusType, MortarRecordFieldable {
  ROPair<JavaBytecodeBindingKey, Binding> type_newInitialBinding();

  Value type_constAsValue(Object data);

  public default EvaluateResult type_cast(
      EvaluationContext evaluationContext, Location location, Value value) {
    if (value.canCastTo(evaluationContext, this)) {
      return (value).castTo(evaluationContext, location, this);
    } else {
      evaluationContext.errors.add(new MortarInvalidCast(location, this));
      return EvaluateResult.error;
    }
  }
}
