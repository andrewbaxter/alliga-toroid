package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;

public class Access extends LanguageValue {
  public final Value base;
  public final Value key;

  public Access(Location id, Value base, Value key) {
    super(id, hasLowerInSubtree(base, key));
    this.base = base;
    this.key = key;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    return ectx.build(
        ectx.record(ectx.evaluate(this.base).access(context, location, ectx.evaluate(this.key))));
  }
}
