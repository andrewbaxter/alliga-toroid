package com.zarbosoft.merman.core;

import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.TextStylable;
import com.zarbosoft.merman.core.display.derived.CourseGroup;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.wall.bricks.BrickEmpty;
import com.zarbosoft.merman.core.wall.bricks.BrickText;
import com.zarbosoft.rendaw.common.ROMap;

public interface Stylist {
  void styleEmpty(Context context, BrickEmpty brick);

  void styleText(Context context, BrickText brick);

  void styleObbox(Context context, ObboxStyle.Stylable obbox, ObboxType type);

  /**
   * Editor only
   *
   * @param context
   * @param text
   */
  void styleBannerText(Context context, Text text);

  /**
   * Editor only
   *
   * @param context
   * @param blank
   * @param meta
   */
  void styleEmptyDisplay(Context context, Blank blank, ROMap<String, Object> meta);

  /**
   * Editor only
   *
   * @param context
   * @param text
   * @param meta
   */
  void styleTextDisplay(Context context, Text text, ROMap<String, Object> meta);

  /**
   * Editor only
   *
   * @param context
   * @param text
   * @param textPad
   */
  void styleChoiceDescription(Context context, TextStylable text, CourseGroup textPad);

  /**
   * Editor only
   *
   * @param context
   * @param text
   * @param type
   */
  void styleMarker(Context context, Text text, MarkerType type);

  ObboxStyle tabStyle();

  public enum MarkerType {
    ERROR
  }

  public enum ObboxType {
    HOVER,
    CURSOR,
    BANNER_BACKGROUND,
    DETAILS_BACKGROUND,
    CHOICE_CURSOR
  }
}
