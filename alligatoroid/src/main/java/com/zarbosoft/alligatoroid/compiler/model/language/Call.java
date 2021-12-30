package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.Value;

public class Call extends LanguageValue {
  public final Value target;
  public final Value argument;

  public Call(Location id, Value target, Value argument) {
    super(id, hasLowerInSubtree(target, argument));
    this.target = target;
    this.argument = argument;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    return ectx.build(
        ectx.record(ectx.evaluate(target).call(context, location, ectx.evaluate(argument))));
  }
}
