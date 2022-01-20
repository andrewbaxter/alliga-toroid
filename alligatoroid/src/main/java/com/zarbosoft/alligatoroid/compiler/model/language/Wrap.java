package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeOther;

public class Wrap extends LanguageElement {
  public Object data;

  public Wrap(Location id, Object data) {
    super(id, false);
    this.data = data;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    if (data instanceof Value) return EvaluateResult.pure((Value) data);
    else return EvaluateResult.pure(new WholeOther(data));
  }
}
