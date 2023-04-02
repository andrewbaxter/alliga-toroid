package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.visual.Vector;

public class VContainer extends MultiChildContainer {
  private final Group group;
  double transverseSpan;
  double converseSpan;

  public VContainer(Context context) {
    group = context.display.group();
  }

  @Override
  protected void relayoutFromChild(Context context, int index) {
    layoutFrom(context, index);
  }

  @Override
  public void setConverseSpan(Context context, double span) {
    converseSpan = span;
    transverseSpan = 0;
    for (Container child : children) {
      child.setPosition(new Vector(0, transverseSpan), true);
      child.setConverseSpan(context, span);
      transverseSpan += child.transverseSpan();
    }
  }

  @Override
  public void add(Context context, Container child) {
    super.add(context, child);
    group.add(child);
    child.setConverseSpan(context, converseSpan);
  }

  @Override
  public void set(Context context, int index, Container child) {
    super.set(context, index, child);
    group.removeAt(index);
    group.add(index, child);
    child.setConverseSpan(context, converseSpan);
  }

  private void layoutFrom(Context context, int index) {
    if (children.isEmpty()) {
        return;
    }
    double transverse = 0;
    if (index > 0) {
      transverse = children.get(index - 1).transverseEdge();
    }
    for (int i = index; i < children.size(); ++i) {
      final Container child = children.get(i);
      child.setPosition(new Vector(0, transverse), false);
      transverse += child.transverseSpan();
    }
    transverseSpan = transverse;
    if (parent != null) {
        parent.relayout(context);
    }
  }

  @Override
  protected void remove(Context context, int index) {
    super.remove(context, index);
    group.removeAt(index);
    layoutFrom(context, index);
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
    return transverseSpan;
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
