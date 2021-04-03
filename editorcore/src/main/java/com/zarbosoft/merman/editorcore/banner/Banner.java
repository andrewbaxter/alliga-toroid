package com.zarbosoft.merman.editorcore.banner;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.DelayEngine;
import com.zarbosoft.merman.core.IterationContext;
import com.zarbosoft.merman.core.IterationTask;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.editorcore.displayderived.Box;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.core.wall.Attachment;
import com.zarbosoft.merman.core.wall.Bedding;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.Wall;
import com.zarbosoft.merman.core.syntax.style.Style;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Banner {
  private final PriorityQueue<BannerMessage> queue =
      new PriorityQueue<>(
          11,
          new Comparator<BannerMessage>() {
            @Override
            public int compare(BannerMessage a, BannerMessage b) {
              return -Double.compare(a.priority, b.priority);
            }
          });
  private final Attachment attachment = new TransverseListener(this);
  private final Style style;
  public Text text;
  public Box background;
  private DelayEngine.Handle timer = null;
  private BannerMessage current;
  private Brick brick;
  private double transverse;
  private double scroll;
  private Bedding bedding;
  private IterationPlace idle;

  public Banner(final Context context, Style style) {
    this.style = style;
    context.wall.addCornerstoneListener(
        context,
        new Wall.CornerstoneListener() {
          @Override
          public void cornerstoneChanged(final Context context, final Brick cornerstone) {
            if (brick != null) {
              brick.removeAttachment(attachment);
            }
            brick = cornerstone;
            brick.addAttachment(context, attachment);
          }
        });
    context.addConverseEdgeListener(
        new Context.ContextDoubleListener() {
          @Override
          public void changed(final Context context, final double oldValue, final double newValue) {
            resizeBackground(context);
          }
        });
  }

  private void idlePlace(final Context context, final boolean animate) {
    if (text == null) return;
    if (idle == null) {
      idle = new IterationPlace(context);
      context.addIteration(idle);
    }
    idle.animate = idle.animate && animate;
  }

  public void setScroll(final Context context, final double scroll) {
    this.scroll = scroll;
    idlePlace(context, true);
  }

  private void place(final Context context, final boolean animate) {
    if (text == null) return;
    final double calculatedTransverse =
        transverse - text.descent() - context.syntax.bannerPad.transverseEnd - scroll;
    text.setBaselinePosition(
        new Vector(context.syntax.bannerPad.converseStart, calculatedTransverse), animate);
    if (background != null)
      background.setPosition(
          new Vector(0, calculatedTransverse - text.ascent()), animate);
  }

  private void resizeBackground(final Context context) {
    if (background == null) return;
    background.setSize(context, context.edge * 2, text.descent() + text.ascent());
  }

  public void addMessage(final Context context, final BannerMessage message) {
    if (queue.isEmpty()) {
      if (style.box != null) {
        background = new Box(context);
        context.midground.add(background.drawing);
      }
      text = context.display.text();
      context.midground.add(text);
      updateStyle(context);
      resizeBackground(context);
    }
    queue.add(message);
    update(context);
  }

  private void updateStyle(final Context context) {
    if (text == null) return;
    background.setStyle(style.box);
    text.setFont(context, Context.getFont(context, style));
    text.setColor(context, style.color);
    if (bedding != null) context.wall.removeBedding(context, bedding);
    bedding =
        new Bedding(
            text.transverseSpan()
                + context.syntax.bannerPad.transverseStart
                + context.syntax.bannerPad.transverseEnd,
            0);
    context.wall.addBedding(context, bedding);
    idlePlace(context, true);
  }

  private void update(final Context context) {
    if (queue.isEmpty()) {
      if (text != null) {
        context.midground.remove(text);
        text = null;
        if (background != null) {
          context.midground.remove(background.drawing);
          background = null;
        }
        context.wall.removeBedding(context, bedding);
        bedding = null;
      }
    } else if (queue.peek() != current) {
      current = queue.peek();
      text.setText(context, current.text);
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
      if (current.duration != 0)
        timer =
            context.delayEngine.delay(
                current.duration,
                () -> context.addIteration(new IterationNextPage(Banner.this, context)));
    }
  }

  public void destroy() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  public void removeMessage(final Context context, final BannerMessage message) {
    if (queue.isEmpty())
      return; // TODO implement message destroy cb, extraneous removeMessages unnecessary
    queue.remove(message);
    if (queue.isEmpty() && timer != null) {
      timer.cancel();
      timer = null;
    }
    update(context);
  }

  private static class IterationNextPage extends IterationTask {
    private final Context context;
    private final Banner banner;

    public IterationNextPage(Banner banner, Context context) {
      this.context = context;
      this.banner = banner;
    }

    @Override
    protected boolean runImplementation(final IterationContext iterationContext) {
      banner.queue.poll();
      banner.update(context);
      return false;
    }

    @Override
    protected void destroyed() {}
  }

  private static class TransverseListener extends Attachment {
    private final Banner banner;

    public TransverseListener(Banner banner) {
      this.banner = banner;
    }

    @Override
    public void setTransverse(final Context context, final double transverse) {
      banner.transverse = transverse;
      banner.idlePlace(context, false);
    }

    @Override
    public void destroy(final Context context) {
      banner.brick = null;
    }
  }

  private class IterationPlace extends IterationTask {
    private final Context context;
    public boolean animate;

    private IterationPlace(final Context context) {
      this.context = context;
      animate = context.animateCoursePlacement;
    }

    @Override
    protected boolean runImplementation(final IterationContext iterationContext) {
      place(context, animate);
      return false;
    }

    @Override
    protected void destroyed() {
      idle = null;
    }
  }
}
