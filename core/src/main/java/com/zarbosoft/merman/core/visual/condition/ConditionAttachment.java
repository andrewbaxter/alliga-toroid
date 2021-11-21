package com.zarbosoft.merman.core.visual.condition;

import com.zarbosoft.merman.core.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class ConditionAttachment {
  private final List<Listener> listeners = new ArrayList<>();
  private final boolean invert;
  private boolean show;

  protected ConditionAttachment(final boolean invert, boolean initialShow) {
    this.invert = invert;
    this.show = invert ? !initialShow : initialShow;
  }

  public void register(final Listener listener) {
    listeners.add(listener);
  }

  public boolean show() {
    return show;
  }

  public abstract void destroy(Context context);

  public void setState(final Context context, boolean show) {
    if (invert) show = !show;
    if (this.show == show) return;
    this.show = show;
    for (Listener listener : listeners) {
      listener.conditionChanged(context, show);
    }
  }

  public interface Listener {
    void conditionChanged(Context context, boolean show);
  }
}
