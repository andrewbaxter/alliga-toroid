package com.zarbosoft.merman.core.wall.bricks;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Font;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;

public class BrickText extends Brick {
  private final double toPixels;
  public Text text;
  private Font font;
  private Double overrideDescent;
  private Double overrideAscent;
  private Padding padding = Padding.empty;

  public BrickText(
      final Context context,
      final BrickInterface inter,
      SplitMode splitMode,
      String alignmentId,
      String splitAlignmentId,
      int hackAvoidChanged) {
    super(inter, splitMode, alignmentId, splitAlignmentId);
    toPixels = context.toPixels;
    text = context.display.text();
    setFont(context, null, 14);
  }

  public BrickText(
      final Context context,
      final BrickInterface inter,
      SplitMode splitMode,
      String alignmentId,
      String splitAlignmentId) {
    this(context, inter, splitMode, alignmentId, splitAlignmentId, 0);
    layoutPropertiesChanged(context);
  }

  public void setColor(Context context, ModelColor color) {
    text.setColor(context, color);
  }

  public void setFont(Context context, String fontName, double fontSize) {
    font = Context.getFont(context, fontName, fontSize);
    text.setFont(context, font);
  }

  @Override
  public double descent() {
    if (overrideDescent != null) return overrideDescent * toPixels;
    return text.descent() + padding.transverseEnd * toPixels;
  }

  @Override
  public double ascent() {
    if (overrideAscent != null) return overrideAscent * toPixels;
    return text.ascent() + padding.converseStart * toPixels;
  }

  @Override
  public double converseEdge() {
    return getConverse() + converseSpan;
  }

  @Override
  public double converseSpan() {
    return converseSpan;
  }

  @Override
  public CourseDisplayNode getDisplayNode() {
    return text;
  }

  @Override
  public void setConverse(final Context context, final double minConverse, final double converse) {
    this.preAlignConverse = minConverse;
    text.setConverse(padding.converseStart * toPixels + converse);
  }

  @Override
  public void allocateTransverse(final Context context, final double ascent, final double descent) {
    text.setBaselineTransverse(ascent);
  }

  public void setText(final Context context, final String text) {
    this.text.setText(context, text.replaceAll("\\p{Cntrl}", context.syntax.unprintable));
    recalculateSpan(context);
  }

  private void recalculateSpan(Context context) {
    this.converseSpan =
        font.measurer().getWidth(this.text.text())
            + padding.converseStart * toPixels
            + padding.converseEnd * toPixels;
    layoutPropertiesChanged(context);
  }

  @Override
  public void restyle(Context context) {
    context.stylist.styleText(context, this);
  }

  @Override
  public double getConverse() {
    return text.converse() - padding.converseStart * toPixels;
  }

  public Font getFont() {
    return text.font();
  }

  public double getConverseOffset(final int index) {
    return text.getConverseAtIndex(index);
  }

  public int getUnder(final Context context, final Vector point) {
    return text.getIndexAtConverse(context, point.converse);
  }

  public void setOverrideDescent(Context context, Double overrideDescent) {
    this.overrideDescent = overrideDescent;
    layoutPropertiesChanged(context);
  }

  public void setOverrideAscent(Context context, Double overrideAscent) {
    this.overrideAscent = overrideAscent;
    layoutPropertiesChanged(context);
  }

  public void setPadding(Context context, Padding padding) {
    this.padding = padding;
    recalculateSpan(context);
  }
}
