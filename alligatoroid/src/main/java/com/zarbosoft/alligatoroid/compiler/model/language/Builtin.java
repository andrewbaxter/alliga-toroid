package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Builtin extends LanguageElement {
  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(Meta.builtin);
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return false;
  }
}
