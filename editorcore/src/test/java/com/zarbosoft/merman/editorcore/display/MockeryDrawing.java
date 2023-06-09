package com.zarbosoft.merman.editorcore.display;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Drawing;
import com.zarbosoft.merman.core.display.DrawingContext;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.visual.Vector;

public class MockeryDrawing extends MockFreeDisplayNode implements Drawing {
  private Vector size = new Vector(0, 0);

  @Override
  public double transverseSpan() {
    return size.transverse;
  }

  @Override
  public double converseSpan() {
    return size.converse;
  }

  @Override
  public Object inner_() {
    return null;
  }

  @Override
  public void clear() {}

  @Override
  public void resize(final Context context, final Vector vector) {
    MockeryDrawing.this.size = vector;
  }

  @Override
  public DrawingContext begin(final Context context) {
    return new DrawingContext() {
      @Override
      public void setLineColor(final ModelColor color) {}

      @Override
      public void setLineCapRound() {}

      @Override
      public void setLineThickness(final double lineThickness) {}

      @Override
      public void setLineCapFlat() {}

      @Override
      public void setFillColor(final ModelColor color) {}

      @Override
      public void beginStrokePath() {}

      @Override
      public void beginFillPath() {}

      @Override
      public void moveTo(final double halfBuffer, final double halfBuffer1) {}

      @Override
      public void lineTo(final double i, final double i1) {}

      @Override
      public void splineTo(double h1c, double h1t, double h2c, double h2t, double c, double t) {}

      @Override
      public void closePath() {}

      @Override
      public void arcTo(
          final double c, final double t, final double c2, final double t2, final double radius) {}

      @Override
      public void translate(final double c, final double t) {}
    };
  }

  @Override
  public void setPosition(Vector vector, boolean animate) {
    transverse = vector.transverse;
    converse = vector.converse;
  }
}
