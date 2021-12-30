package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.ROPair;

public class Bind extends LanguageValue {
  public final Value key;
  public final Value value;

  public Bind(Location id, Value key, Value value) {
    super(id, hasLowerInSubtree(key, value));
    this.key = key;
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue key = WholeValue.getWhole(context, location, ectx.evaluate(this.key));
    Value value = ectx.evaluate(this.value);
    if (key == null) {
      context.scope.error = true;
      return EvaluateResult.error;
    }
    Binding old = context.scope.remove(key);
    if (old != null) {
      ectx.recordPre(old.drop(context, location));
    }
    ROPair<TargetCode, Binding> bound = value.bind(context, location);
    context.scope.put(key, bound.second);
    ectx.recordPre(bound.first);
    return ectx.build(NullValue.value);
  }
}
