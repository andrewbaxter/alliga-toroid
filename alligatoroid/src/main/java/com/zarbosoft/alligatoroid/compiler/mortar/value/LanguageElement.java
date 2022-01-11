package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public abstract class LanguageElement {
  public final Location location;
  public final boolean hasLowerInSubtree;

  public LanguageElement(Location id, boolean hasLowerInSubtree) {
    this.location = id;
    this.hasLowerInSubtree = hasLowerInSubtree;
  }

  protected static boolean hasLowerInSubtree(ROList<Object> elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement)
        out = out || ((LanguageElement) element).hasLowerInSubtree;
    }
    return out;
  }

  protected static boolean hasLowerInSubtree(Object... elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement)
        out = out || ((LanguageElement) element).hasLowerInSubtree;
    }
    return out;
  }

  public abstract EvaluateResult evaluate(EvaluationContext context);

  public Location location() {
    return location;
  }
}
