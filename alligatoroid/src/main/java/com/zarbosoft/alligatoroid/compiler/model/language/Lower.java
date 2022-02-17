package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.error.LowerTooDeep;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public final class Lower extends LanguageElement {
  @Param public LanguageElement child;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return true;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    context.moduleContext.errors.add(new LowerTooDeep(id));
    return EvaluateResult.error;
  }
}
