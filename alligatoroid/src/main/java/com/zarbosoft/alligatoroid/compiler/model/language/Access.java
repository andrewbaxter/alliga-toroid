package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Access extends LanguageElement {
  @Param public LanguageElement base;
  @Param public LanguageElement key;

  @Override
  protected boolean innerHasLowerInSubtree() {
  return hasLowerInSubtree(base, key);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    return ectx.build(
        ectx.record(ectx.evaluate(this.base).access(context, id, ectx.evaluate(this.key))));
  }
}
