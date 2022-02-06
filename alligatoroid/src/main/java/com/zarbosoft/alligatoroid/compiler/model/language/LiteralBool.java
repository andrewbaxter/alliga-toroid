package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class LiteralBool extends LanguageElement {
  public final boolean value;

  public LiteralBool(Location id, boolean value) {
    super(id, false);
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(new ConstBool(value));
  }
}
