package com.zarbosoft.merman.core.visual.visuals;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Hoverable;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.attachments.BorderAttachment;

abstract class HoverableFieldArrayBase extends Hoverable {
  public final VisualFieldArray visual;
  final BorderAttachment border;

  HoverableFieldArrayBase(final Context context, VisualFieldArray visual) {
    this.visual = visual;
    border = new BorderAttachment(context);
    context.stylist.styleObbox(context, border, Stylist.ObboxType.HOVER);
  }

  @Override
  protected void clear(final Context context) {
    border.destroy(context);
    if (visual.hoverable == this) visual.hoverable = null;
  }

  @Override
  public Visual visual() {
    return visual;
  }

  public abstract void notifyRangeAdjusted(Context context, int index, int removed, int added);

  public abstract void notifySelected(Context context, int start, int end);
}
