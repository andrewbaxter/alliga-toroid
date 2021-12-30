package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.rendaw.common.Assertion;

public class RecordElement extends LanguageValue {
  public final Value key;
  public final Value value;

  public RecordElement(Location id, Value key, Value value) {
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
