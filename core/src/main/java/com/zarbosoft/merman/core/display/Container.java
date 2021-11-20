package com.zarbosoft.merman.core.display;

import com.zarbosoft.merman.core.Context;

/**
 * A container occupies a space and allocates that space to children. setConverseSpan will always be
 * called once immediately, so initial layout can happen there.
 */
public interface Container extends FreeDisplayNode {
  public void setConverseSpan(Context context, double span);

  public Parent getParent();

  public void setParent(Parent parent);

  public default void removeFromParent(Context context) {
    getParent().remove(context);
  }

  public interface Parent {
    void relayout(Context context);

    void remove(Context context);
  }
}
