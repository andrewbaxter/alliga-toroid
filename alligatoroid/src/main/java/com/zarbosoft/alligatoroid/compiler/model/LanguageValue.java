package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoGraphBuiltinValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.ROList;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public abstract class LanguageValue
    implements SimpleValue,  AutoGraphBuiltinValue {
  public final Location location;
  public final boolean hasLowerInSubtree;

  public LanguageValue(Location id, boolean hasLowerInSubtree) {
    this.location = id;
    this.hasLowerInSubtree = hasLowerInSubtree;
  }

  protected static boolean hasLowerInSubtree(ROList<Value> values) {
    boolean out = false;
    for (Value value : values) {
      if (value instanceof LanguageValue) out = out || ((LanguageValue) value).hasLowerInSubtree;
    }
    return out;
  }

  protected static boolean hasLowerInSubtree(Value... values) {
    boolean out = false;
    for (Value value : values) {
      if (value instanceof LanguageValue) out = out || ((LanguageValue) value).hasLowerInSubtree;
    }
    return out;
  }

  @Override
  public abstract EvaluateResult evaluate(EvaluationContext context);

  @Override
  public Location location() {
    return location;
  }
}
