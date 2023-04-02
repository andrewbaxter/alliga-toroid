package com.zarbosoft.merman.jfxcore.display;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Display;
import com.zarbosoft.merman.core.display.Font;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.DeadCode;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public class JavaFXText implements Text {
  protected final javafx.scene.text.Text text;
  protected final JavaFXDisplay display;
  private double transverseBaseline;
  private double ascent;
  private double descent;
  private double converse;
  private Transition transition;

  protected JavaFXText(JavaFXDisplay display) {
    this.display = display;
    this.text = new javafx.scene.text.Text();
    this.text.setMouseTransparent(true);
    ((javafx.scene.text.Text) text).setTextOrigin(VPos.BASELINE);
  }

  private void transition(Transition newTransition) {
    if (transition != null) {
        transition.stop();
    }
    transition = newTransition;
    transition.setOnFinished(
        e -> {
          transition = null;
        });
    transition.play();
  }

  @Override
  public Node inner_() {
    return text;
  }

  @Override
  public String text() {
    return ((javafx.scene.text.Text) text).getText();
  }

  public void updateMetrics() {
    javafx.scene.text.Text text = (javafx.scene.text.Text) this.text;
    ascent = text.getBaselineOffset();
    final Bounds bounds = text.getLayoutBounds();
    final double height = bounds.getMaxY() - bounds.getMinY();
    descent = height - ascent;
    fixPosition(false);
  }

  public void setText(final Context context, final String newText) {
    ((javafx.scene.text.Text) text).setText(newText);
    updateMetrics();
  }

  @Override
  public void setColor(final Context context, final ModelColor color) {
    Paint convert = Helper.convert(color);
    ((javafx.scene.text.Text) text).setFill(convert);
  }

  @Override
  public Font font() {
    return new JavaFXFont(((javafx.scene.text.Text) text).getFont());
  }

  @Override
  public void setFont(final Context context, final Font font) {
    ((javafx.scene.text.Text) text).setFont(((JavaFXFont) font).font);
    updateMetrics();
  }

  public int getIndexAtConverse(final Context context, final double converse) {
    switch (context.syntax.converseDirection) {
      case UP:
      case DOWN:
        return text.hitTest(new Point2D(text.getX(), converse - this.converse)).getInsertionIndex();
      case LEFT:
      case RIGHT:
        return text.hitTest(new Point2D(converse - this.converse, text.getY())).getInsertionIndex();
    }
    throw new DeadCode();
  }

  public double getConverseAtIndex(final int index) {
    if (index == 0) {
        return 0;
    }
    Font.Measurer measurer = font().measurer();
    final double precedingLength = measurer.getWidth(text.getText().substring(0, index));
    int charStart = Math.max(0, index - 1);
    int charEnd = Math.min(text.getText().length(), Math.max(1, index));
    String charSubstr = text.getText().substring(charStart, charEnd);
    final double charLength = measurer.getWidth(charSubstr);
    return (int) (precedingLength - charLength * 0.2);
  }

  @Override
  public void setBaselineTransverse(double transverse, boolean animate) {
    this.transverseBaseline = transverse;
    setJFXPositionInternal(display.convert.unconvertTransverse(transverse, 0, 0), animate);
  }

  @Override
  public void setBaselinePosition(Vector vector, boolean animate) {
    this.transverseBaseline = vector.transverse;
    this.converse = vector.converse;
    fixPosition(animate);
  }

  public void fixPosition(boolean animate) {
    Bounds bounds = text.getLayoutBounds();
    Display.UnconvertVector v =
        display.convert.unconvert(converse, transverseBaseline, bounds.getWidth(), 0);
    if (animate) {
        transition(
            new TransitionSmoothOut(
                text,
                v.x - (text.getLayoutX() + text.getTranslateX()),
                v.y - (text.getLayoutY() + text.getTranslateY())));
    }
    text.setLayoutX(v.x);
    text.setLayoutY(v.y);
  }

  @Override
  public double baselineTransverse() {
    return transverseBaseline;
  }

  @Override
  public double ascent() {
    return ascent;
  }

  @Override
  public double descent() {
    return descent;
  }

  @Override
  public double converse() {
    return converse;
  }

  @Override
  public double converseSpan() {
    return text.getLayoutBounds().getWidth();
  }

  @Override
  public void setConverse(double converse, boolean animate) {
    this.converse = converse;
    setJFXPositionInternal(display.convert.unconvertConverse(converse, 0, 0), animate);
  }

  public void setJFXPositionInternal(final Display.UnconvertAxis v, final boolean animate) {
    if (v.x) {
      if (animate) {
          transition(
              new TransitionSmoothOut(
                  text, v.amount - (text.getLayoutX() + text.getTranslateX()), null));
      }
      text.setLayoutX(v.amount);
    } else {
      if (animate) {
          transition(
              new TransitionSmoothOut(
                  text, null, v.amount - (text.getLayoutY() + text.getTranslateY())));
      }
      text.setLayoutY(v.amount);
    }
  }
}
