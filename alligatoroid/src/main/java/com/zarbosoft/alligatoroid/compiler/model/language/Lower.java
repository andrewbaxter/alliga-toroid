package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.LowerTooDeep;

public final class Lower extends LanguageValue {
  public final Value child;

  public Lower(Location id, Value child) {
    super(id, true);
    this.child = child;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    context.moduleContext.errors.add(new LowerTooDeep(location));
    return EvaluateResult.error;
  }
}
