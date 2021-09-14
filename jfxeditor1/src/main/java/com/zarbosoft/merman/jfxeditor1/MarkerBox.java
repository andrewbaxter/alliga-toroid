package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.wall.Attachment;

import static com.zarbosoft.merman.core.Stylist.MarkerType.ERROR;
import static com.zarbosoft.merman.jfxeditor1.NotMain.META_KEY_ERROR;

public class MarkerBox implements Attachment {
  public final Atom atom;
  public final double transverseOffset;
  public double transverse;
  public double converse;
  public Text mark;

  public MarkerBox(Context context, Atom atom, double transverseOffset) {
    this.atom = atom;
    this.transverseOffset = context.toPixels * transverseOffset;
  }

  @Override
  public void setConverse(Context context, double converse) {
    this.converse = converse;
    if (mark != null) {
      mark.setConverse(converse);
    }
  }

  @Override
  public void setBaselineTransverse(Context context, double baselineTransverse) {
    this.transverse = baselineTransverse;
    if (mark != null) {
      mark.setBaselineTransverse(baselineTransverse + transverseOffset, false);
    }
  }

  private void clearMark(Context context) {
    if (mark != null) {
      context.background.removeNode(mark);
      mark = null;
    }
  }

  @Override
  public void destroy(Context context) {
    clearMark(context);
  }

  public void update(Context context) {
    if (atom.metaHas(META_KEY_ERROR)) {
      if (mark == null) {
        mark = context.display.text();
        mark.setText(context, "┄");
        context.stylist.styleMarker(context, mark, ERROR);
        mark.setBaselinePosition(new Vector(converse, transverse + transverseOffset), false);
        context.background.add(mark);
      }
    } else {
      if (mark != null) {
        clearMark(context);
      }
    }
  }
}
