package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.visual.Vector;

public class BoxContainer extends StackContainer implements ObboxStyle.Stylable {
  private final Box box;

  public BoxContainer(Context context, Container node) {
    super(context);
    add(box = new Box(context));
    addRoot(node);
  }

  @Override
  public void setConverseSpan(Context context, double span) {
    super.setConverseSpan(context, span);
    box.setSize(context, root.converseSpan(), root.transverseSpan());
    box.setPosition(Vector.zero, false);
  }

  @Override
  public void setStyle(Context context, ObboxStyle style) {
    box.setStyle(context, style);
  }
}
