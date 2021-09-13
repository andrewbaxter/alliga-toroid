package com.zarbosoft.merman.core.example;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.TextStylable;
import com.zarbosoft.merman.core.display.derived.CourseGroup;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.wall.bricks.BrickEmpty;
import com.zarbosoft.merman.core.wall.bricks.BrickText;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

public class DirectStylist implements Stylist {
  public final ObboxStyle cursorStyle;
  public final ObboxStyle hoverStyle;
  public final ObboxStyle bannerBackgroundStyle;
  public final TextStyle bannerTextStyle;
  public final ObboxStyle detailsBackgroundStyle;
  public final ObboxStyle choiceCursorStyle;
  public final TextStyle choiceDescriptionStyle;
  public final TextStyle errorMarkStyle;

  public DirectStylist(
      ObboxStyle cursorStyle,
      ObboxStyle hoverStyle,
      ObboxStyle bannerBackgroundStyle,
      TextStyle bannerTextStyle,
      ObboxStyle detailsBackgroundStyle,
      ObboxStyle choiceCursorStyle,
      TextStyle choiceDescriptionStyle,
      TextStyle errorMarkStyle) {
    this.cursorStyle = cursorStyle;
    this.hoverStyle = hoverStyle;
    this.bannerBackgroundStyle = bannerBackgroundStyle;
    this.bannerTextStyle = bannerTextStyle;
    this.detailsBackgroundStyle = detailsBackgroundStyle;
    this.choiceCursorStyle = choiceCursorStyle;
    this.choiceDescriptionStyle = choiceDescriptionStyle;
    this.errorMarkStyle = errorMarkStyle;
  }

  public static TSMap<String, Object> meta(TextStyle style) {
    return new TSMap<>(m -> m.put("style", style));
  }

  public static ROMap<String, Object> meta(SpaceStyle style) {
    return new TSMap<>(m -> m.put("style", style));
  }

  @Override
  public void styleEmpty(Context context, BrickEmpty brick) {
    SpaceStyle style = (SpaceStyle) brick.meta().getOpt("style");
    if (style == null) return;
    brick.setConverseSpan(context, style.space);
  }

  @Override
  public void styleText(Context context, BrickText brick) {
    TextStyle style = (TextStyle) brick.meta().getOpt("style");
    if (style == null) return;
    Object invalid = brick.getVisual().atomVisual().atom.meta.getOpt("invalid");
    if (invalid != null && style.invalidColor != null) brick.setColor(context, style.invalidColor);
    else brick.setColor(context, style.color);
    brick.setFont(context, style.font, style.fontSize);
    brick.setPadding(context, style.padding);
    if (style.ascent != null) brick.setOverrideAscent(context, style.ascent);
    if (style.descent != null) brick.setOverrideDescent(context, style.descent);
  }

  @Override
  public void styleTextDisplay(Context context, Text text, ROMap<String, Object> meta) {
    TextStyle style = (TextStyle) meta.getOpt("style");
    if (style == null) return;
    text.setColor(context, style.color);
    text.setFont(context, Context.getFont(context, null, style.fontSize));
  }

  @Override
  public void styleChoiceDescription(Context context, TextStylable text, CourseGroup textPad) {
    text.setColor(context, choiceDescriptionStyle.color);
    text.setFont(
        context,
        Context.getFont(context, choiceDescriptionStyle.font, choiceDescriptionStyle.fontSize));
    if (textPad != null) textPad.setPadding(context, choiceDescriptionStyle.padding);
  }

  @Override
  public void styleMarker(Context context, Text text, MarkerType type) {
    switch (type) {
      case ERROR:
        text.setColor(context, errorMarkStyle.color);
        text.setFont(context, Context.getFont(context, null, errorMarkStyle.fontSize));
        break;
      default:
        throw new Assertion();
    }
  }

  @Override
  public void styleObbox(Context context, ObboxStyle.Stylable obbox, ObboxType type) {
    switch (type) {
      case HOVER:
        obbox.setStyle(context, hoverStyle);
        break;
      case CURSOR:
        obbox.setStyle(context, cursorStyle);
        break;
      case BANNER_BACKGROUND:
        obbox.setStyle(context, bannerBackgroundStyle);
        break;
      case DETAILS_BACKGROUND:
        obbox.setStyle(context, detailsBackgroundStyle);
        break;
      case CHOICE_CURSOR:
        obbox.setStyle(context, choiceCursorStyle);
        break;
      default:
        throw new Assertion();
    }
  }

  @Override
  public void styleBannerText(Context context, Text text) {
    text.setColor(context, bannerTextStyle.color);
    text.setFont(context, Context.getFont(context, null, bannerTextStyle.fontSize));
  }

  @Override
  public void styleEmptyDisplay(Context context, Blank blank, ROMap<String, Object> meta) {}

  public static class TextStyle {
    public String font;
    public double fontSize = 14;
    public ModelColor color = ModelColor.RGB.black;
    public ModelColor invalidColor;
    public Padding padding = Padding.empty;
    public Double ascent;
    public Double descent;

    public TextStyle() {}

    public TextStyle font(String font) {
      this.font = font;
      return this;
    }

    public TextStyle color(ModelColor color) {
      this.color = color;
      return this;
    }

    public TextStyle invalidColor(ModelColor invalidColor) {
      this.invalidColor = invalidColor;
      return this;
    }

    public TextStyle descent(Double size) {
      this.descent = size;
      return this;
    }

    public TextStyle ascent(Double size) {
      this.ascent = size;
      return this;
    }

    public TextStyle fontSize(double size) {
      this.fontSize = size;
      return this;
    }

    public TextStyle padding(Padding padding) {
      this.padding = padding;
      return this;
    }
  }

  public static class SpaceStyle {
    public double space;

    public SpaceStyle space(double size) {
      this.space = size;
      return this;
    }
  }
}
