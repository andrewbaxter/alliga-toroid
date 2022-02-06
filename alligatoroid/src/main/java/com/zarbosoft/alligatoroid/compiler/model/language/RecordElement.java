package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

public class RecordElement extends LanguageElement {
  public  LanguageElement key;
  public LanguageElement value;

  public RecordElement(Location id, LanguageElement key, LanguageElement value) {
    super(id, hasLowerInSubtree(key, value));
    this.key = key;
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    // Evaluated by record literal directly
    throw new Assertion();
  }
}
