package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;

public class LiteralString extends LanguageValue {
  public final String value;

  public LiteralString(Location id, String value) {
    super(id, false);
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(new WholeString(value));
  }
}
