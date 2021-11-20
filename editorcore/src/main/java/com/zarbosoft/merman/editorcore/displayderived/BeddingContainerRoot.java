package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.IterationContext;
import com.zarbosoft.merman.core.IterationTask;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.wall.Attachment;
import com.zarbosoft.merman.core.wall.Bedding;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.Wall;
import com.zarbosoft.merman.editorcore.Editor;

public class BeddingContainerRoot {
  private final Wall.CornerstoneListener cornerstoneListener;
  private final Context.ContextDoubleListener edgeListener;
  private final boolean above;
  public Container current;
  private Brick brick;
  private double transverse;
  private double transverseSpan;
  private Bedding bedding;
  private BeddingContainerRoot.IterationPlace idle;
  private final Attachment attachment =
      new Attachment() {
        @Override
        public void setTransverse(final Context context, final double transverse) {
          BeddingContainerRoot.this.transverse = transverse;
          iterationPlace(context, false);
        }

        @Override
        public void destroy(final Context context) {
          brick = null;
        }

        @Override
        public void setTransverseSpan(
            final Context context, final double ascent, final double descent) {
          BeddingContainerRoot.this.transverseSpan = ascent + descent;
          iterationPlace(context, false);
        }
      };

  public BeddingContainerRoot(final Context context, boolean above) {
    this.above = above;
    cornerstoneListener =
        new Wall.CornerstoneListener() {
          @Override
          public void cornerstoneChanged(final Context context, final Brick cornerstone) {
            if (brick != null) {
              brick.removeAttachment(attachment);
            }
            brick = cornerstone;
            brick.addAttachment(context, attachment);
          }
        };
    context.wall.addCornerstoneListener(context, cornerstoneListener);
    edgeListener =
        new Context.ContextDoubleListener() {
          @Override
          public void changed(final Context context, final double oldValue, final double newValue) {
            updateEdge(context);
          }
        };
    context.addConverseEdgeListener(edgeListener);
  }

  private void updateEdge(Context context) {
    if (current == null) return;
    current.setConverseSpan(context, context.edge);
  }

  private void iterationPlace(final Context context, final boolean animate) {
    if (current == null) return;
    if (idle == null) {
      idle = new BeddingContainerRoot.IterationPlace(context);
      context.addIteration(idle);
    }
    idle.animate = idle.animate && animate;
  }

  private void place(final boolean animate) {
    final double transverse = this.transverse + transverseSpan;
    current.setPosition(new Vector(0, transverse), animate);
  }

  public void relayout(Context context) {
    double transverseSpan = current.transverseSpan();
    if (bedding == null
            || (above && bedding.before != transverseSpan)
            || (!above && bedding.after != transverseSpan)) {
      if (bedding != null) {
        context.wall.removeBedding(context, bedding);
      }
      bedding = above ? new Bedding(transverseSpan, 0) : new Bedding(0, transverseSpan);
      context.wall.addBedding(context, bedding);
    }
  }

  public void removeInner(Editor editor, Container inner) {
    if (inner != current) return;
    Context context = editor.context;
    if (current != null) {
      context.midground.removeNode(current);
      context.wall.removeBedding(context, bedding);
      bedding = null;
    }
  }

  public void setInner(Editor editor, Container inner) {
    Context context = editor.context;
    if (current != null) {
      context.midground.removeNode(current);
      context.wall.removeBedding(context, bedding);
      bedding = null;
    }
    current = inner;
    if (current != null) {
      inner.setParent(new Container.Parent() {
        @Override
        public void relayout(Context context) {
          BeddingContainerRoot.this.relayout(context);
        }

        @Override
        public void remove(Context context) {
          removeInner(editor, inner);
        }
      });
      place(false);
      updateEdge(context);
      context.midground.add(current);
    }
  }

  private class IterationPlace extends IterationTask {
    private boolean animate;

    private IterationPlace(final Context context) {
      this.animate = context.animateCoursePlacement;
    }

    @Override
    protected boolean runImplementation(final IterationContext iterationContext) {
      if (current != null) {
        place(animate);
      }
      return false;
    }

    @Override
    protected void destroyed() {
      idle = null;
    }
  }
}
