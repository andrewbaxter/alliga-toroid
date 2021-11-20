package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.FreeDisplayNode;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.Assertion;

public class StackContainer extends SingleChildContainer implements Container, FreeDisplayNode {
  private final Group group;

  public StackContainer(Context context) {
    this.group = context.display.group();
  }

  public StackContainer add(FreeDisplayNode node) {
    this.group.add(node);
    return this;
  }

  @Override
  public void set(Context context, Container node) {
    super.set(context, node);
    this.group.add(node);
  }

  @Override
  protected void remove(Context context) {
    throw new Assertion();
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
    return child.transverseSpan();
  }

  @Override
  public double converseSpan() {
    return child.converseSpan();
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
