package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Vary extends LanguageElement {
  public LanguageElement child;

  public Vary(Location id, LanguageElement child) {
    super(id, true);
    this.child = child;
  }

  @Override
  public <V extends Value> EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    return ectx.build(ectx.record(ectx.evaluate(this.child).vary(context, location)));
  }
}
