package com.zarbosoft.merman.core.visual.visuals;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualLeaf;
import com.zarbosoft.merman.core.visual.VisualParent;
import com.zarbosoft.merman.core.visual.alignment.Alignment;
import com.zarbosoft.merman.core.visual.condition.ConditionAttachment;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;

public class VisualSymbol extends Visual
    implements VisualLeaf, ConditionAttachment.Listener, BrickInterface {
  private final FrontSymbolSpec frontSymbol;
  public VisualParent parent;
  public Brick brick = null;
  public ConditionAttachment condition = null;

  public VisualSymbol(
      final VisualParent parent,
      final FrontSymbolSpec frontSymbolSpec,
      final ConditionAttachment condition,
      final int visualDepth) {
    super(visualDepth);
    this.parent = parent;
    this.frontSymbol = frontSymbolSpec;
    if (condition != null) {
      this.condition = condition;
      condition.register(this);
    }
  }

  @Override
  public void conditionChanged(final Context context, final boolean show) {
    if (show) {
      context.triggerIdleLayBricks(parent, 0, 1, 1, null, null);
    } else if (brick != null) {
      brick.destroy(context);
    }
  }

  @Override
  public VisualParent parent() {
    return parent;
  }

  @Override
  public boolean selectIntoAnyChild(final Context context) {
    return false;
  }

  @Override
  public void notifyLastBrickCreated(Context context, Brick brick) {
    throw new Assertion(); // leaf
  }

  @Override
  public void notifyFirstBrickCreated(Context context, Brick brick) {
    throw new Assertion(); // leaf
  }

  @Override
  public CreateBrickResult createOrGetCornerstoneCandidate(final Context context) {
    if (condition != null)
      /* Cornerstones can't suddenly disappear without cursor changing */ {
        return CreateBrickResult.empty();
    }
    if (brick != null) {
        return CreateBrickResult.brick(brick);
    }
    brick = frontSymbol.type.createBrick(context, this);
    parent.notifyFirstBrickCreated(context, brick);
    parent.notifyLastBrickCreated(context, brick);
    return CreateBrickResult.brick(brick);
  }

  @Override
  public ExtendBrickResult createFirstBrick(final Context context) {
    if (brick != null) {
        return ExtendBrickResult.exists();
    }
    if (condition != null && !condition.show()) {
      return ExtendBrickResult.empty();
    }
    brick = frontSymbol.type.createBrick(context, this);
    parent.notifyFirstBrickCreated(context, brick);
    parent.notifyLastBrickCreated(context, brick);
    return ExtendBrickResult.brick(brick);
  }

  @Override
  public ExtendBrickResult createLastBrick(final Context context) {
    return createFirstBrick(context);
  }

  @Override
  public Brick getFirstBrick(final Context context) {
    return brick;
  }

  @Override
  public Brick getLastBrick(final Context context) {
    return brick;
  }

  @Override
  public void compact(Context context) {
    if (brick != null) {
        brick.layoutPropertiesChanged(context);
    }
  }

  @Override
  public void expand(Context context) {
    if (brick != null) {
        brick.layoutPropertiesChanged(context);
    }
  }

  @Override
  public void getLeafBricks(final Context context, TSList<Brick> bricks) {
    if (brick == null) {
        return;
    }
    bricks.add(brick);
  }

  @Override
  public void root(
      final Context context,
      final VisualParent parent,
      final int visualDepth,
      final int depthScore) {
    super.root(context, parent, visualDepth, depthScore);
    expand(context);
  }

  @Override
  public void uproot(final Context context, final Visual root) {
    if (brick != null) {
        brick.destroy(context);
    }
    if (condition != null) {
        condition.destroy(context);
    }
  }

  @Override
  public VisualLeaf getVisual() {
    return this;
  }

  @Override
  public ExtendBrickResult createPrevious(final Context context) {
    return parent.createPreviousBrick(context);
  }

  @Override
  public ExtendBrickResult createNext(final Context context) {
    return parent.createNextBrick(context);
  }

  @Override
  public void brickDestroyed(final Context context) {
    brick = null;
  }

  @Override
  public Alignment findAlignment(String alignment) {
    return atomVisual().findAlignment(alignment, null);
  }

  @Override
  public ROMap<String, Object> meta() {
    return frontSymbol.type.meta();
  }
}
