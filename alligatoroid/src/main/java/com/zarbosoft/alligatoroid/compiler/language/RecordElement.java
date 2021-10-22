package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.Assertion;

public class RecordElement extends LanguageValue {
  public final Value key;
  public final Value value;
  public Object graphDeserialize(Record data) {
    return graphDeserialize(this.getClass(), data);
  }

  public RecordElement(Location id, Value key, Value value) {
    super(id, hasLowerInSubtree(key, value));
    this.key = key;
    this.value = value;
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    // Evaluated by record literal directly
    throw new Assertion();
  }
}
