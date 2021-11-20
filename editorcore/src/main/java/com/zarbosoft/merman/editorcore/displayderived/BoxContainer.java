package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.Assertion;

public class BoxContainer extends SingleChildContainer implements ObboxStyle.Stylable {
  private final Group group;
  private final Box box;

  public BoxContainer(Context context) {
    group = context.display.group();
    box = new Box(context);
    group.add(box);
  }

  @Override
  protected void remove(Context context) {
    super.remove(context);
    group.removeAt(1);
  }

  @Override
  protected void relayoutParent(Context context) {
    box.setSize(context, child.converseSpan(), child.transverseSpan());
    box.setPosition(Vector.zero, false);
    super.relayoutParent(context);
  }

  @Override
  public void set(Context context, Container child) {
    super.set(context, child);
    group.add(child);
  }

  @Override
  public void setConverseSpan(Context context, double span) {
    super.setConverseSpan(context, span);
    if (child != null) {
      child.setConverseSpan(context, converseSpan);
    } else if (parent != null) {
      box.setPosition(Vector.zero, false);
      parent.relayout(context);
    }
  }

  @Override
  public void setStyle(Context context, ObboxStyle style) {
    box.setStyle(context, style);
  }

  @Override
  public double converse() {
    return group.converse();
  }

  @Override
  public double transverse() {
    return group.transverse();
  }

  @Override
  public double transverseSpan() {
    if (child != null) {
      return child.transverseSpan();
    }
    return 0;
  }

  @Override
  public double converseSpan() {
    return converseSpan;
  }

  @Override
  public void setConverse(double converse, boolean animate) {
    group.setConverse(converse, animate);
  }

  @Override
  public Object inner_() {
    return group.inner_();
  }

  @Override
  public void setPosition(Vector vector, boolean animate) {
    group.setPosition(vector, animate);
  }
}
