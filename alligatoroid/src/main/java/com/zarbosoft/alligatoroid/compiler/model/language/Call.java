package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;

public class Call extends LanguageElement {
  public LanguageElement target;
  public LanguageElement argument;

  public Call(Location id, LanguageElement target, LanguageElement argument) {
    super(id, hasLowerInSubtree(target, argument));
    this.target = target;
    this.argument = argument;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    return ectx.build(
        ectx.record(
            context.target.call(
                context, location, ectx.evaluate(target), ectx.evaluate(argument))));
  }
}
