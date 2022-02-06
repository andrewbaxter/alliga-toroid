package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Access extends LanguageElement {
  public LanguageElement base;
  public LanguageElement key;

  public Access(Location id, LanguageElement base, LanguageElement key) {
    super(id, hasLowerInSubtree(base, key));
    this.base = base;
    this.key = key;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    return ectx.build(
        ectx.record(
            ectx.evaluate(this.base).access(
                context, location, ectx.evaluate(this.key))));
  }
}
