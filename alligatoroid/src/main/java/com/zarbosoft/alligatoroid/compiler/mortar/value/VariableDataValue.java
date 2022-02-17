package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

public interface VariableDataValue extends DataValue, NoExportValue {
  @Override
  default EvaluateResult export(EvaluationContext context, Location location) {
    throw new Assertion();
  }
}
