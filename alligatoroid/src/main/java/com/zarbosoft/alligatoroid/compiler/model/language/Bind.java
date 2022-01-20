package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.ROPair;

public class Bind extends LanguageElement {
  public LanguageElement key;
  public LanguageElement value;

  public Bind(Location id, LanguageElement key, LanguageElement value) {
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
      ectx.recordPre(context.target.drop(context, location, old));
    }
    ROPair<TargetCode, ? extends Binding> bound = context.target.bind(context, location, value);
    context.scope.put(key, bound.second);
    ectx.recordPre(bound.first);
    return ectx.build(NullValue.value);
  }
}
