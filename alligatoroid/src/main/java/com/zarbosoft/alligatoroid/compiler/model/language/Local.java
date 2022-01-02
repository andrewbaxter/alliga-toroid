package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;

public class Local extends LanguageValue {
  public final Value key;

  public Local(Location id, Value key) {
    super(id, hasLowerInSubtree(key));
    this.key = key;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue key = WholeValue.getWhole(context, location, ectx.evaluate(this.key));
    if (key == null) return EvaluateResult.error;
    Binding value = context.scope.get(key);
    if (value == null) {
      context.moduleContext.log.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return ectx.build(ectx.record(value.fork(context, location)));
  }
}
