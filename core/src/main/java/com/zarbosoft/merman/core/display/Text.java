package com.zarbosoft.merman.core.display;

import com.zarbosoft.merman.core.Context;

public interface Text extends CourseDisplayNode, TextStylable {
  String text();

  void setText(Context context, String text);

  Font font();

  /**
   * Get the nearest index to the converse - so halfway through 1 = 0, halfway through last = last
   *
   * @param context
   * @param converse
   * @return
   */
  default int getIndexAtConverse(final Context context, final double converse) {
    return font().measurer().getIndexAtConverse(context, text(), converse);
  }

  double getConverseAtIndex(final int index);
}
