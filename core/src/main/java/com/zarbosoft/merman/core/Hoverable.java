package com.zarbosoft.merman.core;

import com.zarbosoft.merman.core.visual.Visual;

public abstract class Hoverable {
  protected abstract void clear(Context context);

  /**
   * Always returns a path to an atom
   *
   * @return
   */
  public abstract SyntaxPath getSyntaxPath();

  public abstract void select(Context context);

  public abstract Visual visual();
}
