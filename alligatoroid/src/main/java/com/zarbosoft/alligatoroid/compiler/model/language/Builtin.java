package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Builtin extends LanguageElement {
  public Builtin(Location id) {
    super(id, false);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(com.zarbosoft.alligatoroid.compiler.Builtin.builtin);
  }
}
