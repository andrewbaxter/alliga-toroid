package com.zarbosoft.merman.core.wall.bricks;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Hoverable;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.rendaw.common.ROPair;

public class BrickLine extends BrickText {
  public final VisualFieldPrimitive.Line line;

  public BrickLine(
      final Context context,
      final VisualFieldPrimitive.Line line,
      SplitMode splitMode,
      String alignmentId,
      String splitAlignmentId) {
    super(context, line, splitMode, alignmentId, splitAlignmentId, 0);
    this.line = line;
    layoutPropertiesChanged(context);
  }

  @Override
  public boolean isSplit(boolean compact) {
    if (line.index > 0) {
        return true;
    }
    return super.isSplit(compact);
  }

  @Override
  public ROPair<Hoverable, Boolean> hover(final Context context, final Vector point) {
    return line.hover(context, point);
  }

  /*
  public void updateValid(Context context) {
    setColor(context, line.visual.valid ? style.color : style.invalidColor);
  }
  TODO in stylist
   */
}
