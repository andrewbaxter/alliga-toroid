package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Set extends LanguageElement {
  public LanguageElement target;
  public LanguageElement value;

  public Set(Location id, LanguageElement target, LanguageElement value) {
    super(id, hasLowerInSubtree(target, value));
    this.target = target;
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    Value target = ectx.evaluate(this.target);
    Value value = ectx.evaluate(this.value);
    return target.set(context, location, value);
  }
}
