package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.Font;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.TextStylable;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.TSList;

public class TextBlock implements Container, TextStylable {
  private final Group group;
  private final TSList<Text> lines = new TSList<>();
  private Font font;
  private String text;
  private double converseSpan = Double.MAX_VALUE;
  private ModelColor color;
  private double transverseSpan;
  private Parent parent;

  public TextBlock(Context context) {
    this.group = context.display.group();
  }

  public void setText(Context context, String text) {
    this.text = text;
    rewrap(context);
  }

  private void rewrap(Context context) {
    if (font == null) {
        font = Context.getFont(context, null, null);
    }
    Font.Measurer measurer = font.measurer();
    String[] hardLines = text.split("\n");
    double transverse = 0;
    int i = 0;
    for (String hardText : hardLines) {
      int startAt = 0;
      Environment.LineWalker lineWalker = context.env.lineWalker(hardText);
      Environment.GlyphWalker glyphWalker = context.env.glyphWalker(hardText);

      while (startAt < hardText.length()) {
        int splitAt =
            startAt
                + measurer.getIndexAtConverse(context, hardText.substring(startAt), converseSpan);
        splitAt = lineWalker.beforeOrAt(splitAt);
        if (splitAt == startAt) {
          splitAt = glyphWalker.after(startAt);
        }
        Text line;
        if (i >= lines.size()) {
          lines.add(line = context.display.text());
          line.setFont(context, font);
          if (color != null) {
              line.setColor(context, color);
          }
          group.add(line);
        } else {
          line = lines.get(i);
        }
        line.setText(context, hardText.substring(startAt, splitAt));
        transverse += line.ascent();
        line.setBaselineTransverse(transverse);
        transverse += line.descent();
        startAt = splitAt;
        i += 1;
      }
      while (lines.size() > i) {
        group.removeNode(lines.removeLast());
      }
    }
    transverseSpan = transverse;
    if (parent != null) {
        parent.relayout(context);
    }
  }

  @Override
  public void setConverseSpan(Context context, double span) {
    converseSpan = span;
    rewrap(context);
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
    return group.converse();
  }

  @Override
  public double transverse() {
    return group.transverse();
  }

  @Override
  public double transverseSpan() {
    return transverseSpan;
  }

  @Override
  public double converseSpan() {
    return converseSpan;
  }

  @Override
  public void setConverse(double converse, boolean animate) {
    group.setConverse(converse, animate);
  }

  @Override
  public Object inner_() {
    return group.inner_();
  }

  @Override
  public void setPosition(Vector vector, boolean animate) {
    group.setPosition(vector, animate);
  }

  @Override
  public void setColor(Context context, ModelColor color) {
    this.color = color;
    for (Text line : lines) {
      line.setColor(context, color);
    }
  }

  @Override
  public void setFont(Context context, Font font) {
    this.font = font;
    for (Text line : lines) {
      line.setFont(context, font);
    }
  }
}
