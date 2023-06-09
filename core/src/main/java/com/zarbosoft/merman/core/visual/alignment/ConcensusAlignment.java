package com.zarbosoft.merman.core.visual.alignment;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.IterationContext;
import com.zarbosoft.merman.core.IterationTask;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.wall.Brick;

public class ConcensusAlignment extends Alignment {
  private IterationAlign iterationAlign;

  private void iterationAlign(final Context context) {
    if (iterationAlign == null) {
      iterationAlign = new IterationAlign(context);
      context.addIteration(iterationAlign);
    }
  }

  @Override
  public void destroy(final Context context) {
    if (iterationAlign != null) {
        iterationAlign.destroy();
    }
  }

  @Override
  public void removeBrick(Context context, Brick brick) {
    super.removeBrick(context, brick);
    if (brick.getPreAlignConverse() == converse) {
        iterationAlign(context);
    }
  }

  @Override
  public void feedback(final Context context, final double gotConverse) {
    if (gotConverse > converse) {
        iterationAlign(context);
    }
  }

  @Override
  public void root(final Context context, final VisualAtom atom) {}

  private class IterationAlign extends IterationTask {
    private final Context context;

    private IterationAlign(final Context context) {
      this.context = context;
    }

    @Override
    protected boolean runImplementation(final IterationContext iterationContext) {
      final double oldConverse = converse;
      double max = 0;
      for (Brick brick : bricks) {
        max = Math.max(max, brick.getPreAlignConverse());
      }
      converse = max;
      if (oldConverse != converse) {
        changed(context);
      }
      return false;
    }

    @Override
    protected void destroyed() {
      iterationAlign = null;
    }
  }
}
