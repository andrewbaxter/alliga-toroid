package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.Assertion;

public class PadContainer implements Container {
  private final double padConverseStart;
  private final double padConverseEnd;
  private final double padTransverseStart;
  private final double padTransverseEnd;
  private final Container root;
  private Parent parent;

  public PadContainer(Context context, Padding padding, Container root) {
    this.padConverseStart = padding.converseStart * context.toPixels;
    this.padConverseEnd = padding.converseEnd * context.toPixels;
    this.padTransverseStart = padding.transverseStart * context.toPixels;
    this.padTransverseEnd = padding.transverseEnd * context.toPixels;
    this.root = root;
    root.setParent(
        new Parent() {
          @Override
          public void relayout(Context context) {
            if (PadContainer.this.parent != null) {
              PadContainer.this.parent.relayout(context);
            }
          }

          @Override
          public void remove(Context context) {
            throw new Assertion();
          }
        });
  }

  public void setConverseSpan(Context context, double span) {
    root.setConverseSpan(context, span - padConverseStart - padConverseEnd);
  }

  @Override
  public Parent getParent() {
    return parent;
  }

  @Override
  public void setParent(Parent parent) {
    this.parent = parent;
  }

  @Override
  public double converse() {
    return root.converse() - padConverseStart;
  }

  @Override
  public double transverse() {
    return root.transverse() - padTransverseStart;
  }

  @Override
  public double transverseSpan() {
    return padTransverseStart + root.transverseSpan() + padTransverseEnd;
  }

  @Override
  public double converseSpan() {
    return padConverseStart + root.converseSpan() + padConverseEnd;
  }

  @Override
  public void setConverse(double converse, boolean animate) {
    root.setConverse(padConverseStart + converse, animate);
  }

  @Override
  public Object inner_() {
    return root.inner_();
  }

  @Override
  public void setPosition(Vector vector, boolean animate) {
    root.setPosition(
        new Vector(vector.converse + padConverseStart, vector.transverse + padTransverseStart),
        animate);
  }
}
