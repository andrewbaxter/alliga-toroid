package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.rendaw.common.Assertion;

public abstract class SingleChildContainer implements Container {
  protected Container.Parent parent;
  protected Container child = null;
  protected double converseSpan;

  @Override
  public void setConverseSpan(Context context, double span) {
    this.converseSpan = span;
  }

  @Override
  public Container.Parent getParent() {
    return parent;
  }

  @Override
  public void setParent(Container.Parent parent) {
    this.parent = parent;
  }

  public void set(Context context, Container child) {
    if (this.child != null) {
        throw new Assertion();
    }
    this.child = child;
    child.setParent(new Parent(this));
    child.setConverseSpan(context, converseSpan);
  }

  protected void relayoutParent(Context context) {
    if (parent != null) {
      parent.relayout(context);
    }
  }

  protected void remove(Context context) {
    this.child = null;
  }

  public static class Parent implements Container.Parent {
    private final SingleChildContainer self;

    public Parent(SingleChildContainer self) {
      this.self = self;
    }

    @Override
    public void relayout(Context context) {
      self.relayoutParent(context);
    }

    @Override
    public void remove(Context context) {
      self.remove(context);
    }
  }
}
