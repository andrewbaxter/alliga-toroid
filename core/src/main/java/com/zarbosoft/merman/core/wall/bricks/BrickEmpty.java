package com.zarbosoft.merman.core.wall.bricks;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;

public class BrickEmpty extends Brick {
  private final Blank visual;
  private double ascent;
  private double descent;
  private double converse = 0;

  public BrickEmpty(
      final Context context,
      final BrickInterface inter,
      SplitMode splitMode,
      String alignmentId,
      String splitAlignmentId) {
    super(inter, splitMode, alignmentId, splitAlignmentId);
    visual = context.display.blank();
    layoutPropertiesChanged(context);
  }

  @Override
  public double converseEdge() {
    return converse + converseSpan;
  }

  @Override
  public double converseSpan() {
    return converseSpan;
  }

  @Override
  public CourseDisplayNode getDisplayNode() {
    return visual;
  }

  @Override
  public void setConverse(final Context context, final double minConverse, final double converse) {
    this.preAlignConverse = minConverse;
    this.converse = converse;
    visual.setBaselinePosition(new Vector(converse, 0), false);
  }

  @Override
  public void allocateTransverse(
      final Context context, final double ascent, final double descent) {}

  @Override
  public double descent() {
    return descent;
  }

  @Override
  public double ascent() {
    return ascent;
  }

  @Override
  public void restyle(Context context) {
    context.stylist.styleEmpty(context, this);
  }

  @Override
  public double getConverse() {
    return converse;
  }

  public void setAscent(Context context, double ascent) {
    this.ascent = ascent;
    layoutPropertiesChanged(context);
  }

  public void setDescent(Context context, double descent) {
    this.descent = descent;
    layoutPropertiesChanged(context);
  }

  public void setConverseSpan(Context context, double span) {
    this.converseSpan = span;
    layoutPropertiesChanged(context);
  }
}
