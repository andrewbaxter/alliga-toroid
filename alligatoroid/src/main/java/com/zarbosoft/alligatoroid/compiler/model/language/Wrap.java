package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;

public class Wrap extends LanguageElement {
  @Param public Value value;

  public static Wrap create(Location id, Value value) {
    final Wrap wrap = new Wrap();
    wrap.id = id;
    wrap.value = value;
    wrap.postInit();
    return wrap;
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return false;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(value);
  }
}
