package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.rendaw.common.TSList;

public abstract class MultiChildContainer implements Container {
  protected final TSList<Container> children = new TSList<>();
  protected Container.Parent parent;

  @Override
  public Container.Parent getParent() {
    return parent;
  }

  @Override
  public void setParent(Container.Parent parent) {
    this.parent = parent;
  }

  public void add(Context context, Container child) {
    final Parent parent = new Parent(this);
    parent.index = children.size();
    child.setParent(parent);
    children.add(child);
  }

  protected void relayoutFromChild(Context context, int index) {
    if (parent != null) {
      parent.relayout(context);
    }
  }

  protected void remove(Context context, int index) {
    children.remove(index);
    for (int i = index; i < children.size(); ++i) {
      final Container comp = children.get(i);
      ((Parent) comp.getParent()).index = i - 1;
    }
  }

  public void set(Context context, int index, Container child) {
    children.set(index, child);
    final Parent parent = new Parent(this);
    parent.index = index;
    child.setParent(parent);
  }

  public static class Parent implements Container.Parent {
    private final MultiChildContainer self;
    int index;

    public Parent(MultiChildContainer self) {
      this.self = self;
    }

    @Override
    public void relayout(Context context) {
      self.relayoutFromChild(context, index);
    }

    @Override
    public void remove(Context context) {
      self.remove(context, index);
    }
  }
}
