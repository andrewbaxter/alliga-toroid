package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.symbol.Symbol;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualParent;
import com.zarbosoft.merman.core.visual.visuals.VisualSymbol;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontSymbolSpec extends FrontSpec {
  public final Symbol type;
  /** Nullable */
  public final ConditionType condition;

  @Override
  public void finish(MultiError errors, SyntaxPath typePath, AtomType atomType, TSSet<String> middleUsed) {
    super.finish(errors, typePath, atomType, middleUsed);
    if (condition != null) condition.finish(errors, typePath, atomType);
    if (type != null ) type.finish(errors, typePath, atomType);
  }

  /**
   * Non-null, "" okay. When filling a gap: Text to match non-text symbols, or override the text of
   * text symbols
   */
  public final String gapKey;

  public FrontSymbolSpec(Config config) {
    type = config.type;
    condition = config.condition;
    gapKey = config.gapKey;
  }

  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final int visualDepth,
      final int depthScore) {
    return createVisual(context, parent, null, visualDepth, depthScore);
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final int visualDepth,
      final int depthScore) {
    return new VisualSymbol(
        parent,
        this,
        atom == null ? null : condition == null ? null : condition.create(context, atom),
        visualDepth);
  }

  @Override
  public String fieldId() {
    return null;
  }

  @Override
  public void dispatch(final DispatchHandler handler) {
    handler.handle(this);
  }

  public CourseDisplayNode createDisplay(final Context context) {
    final CourseDisplayNode out = type.createDisplay(context);
    return out;
  }

  public static class Config {
    public final Symbol type;
    public ConditionType condition;
    public String gapKey;

    public Config(Symbol type) {
      this.type = type;
    }

    public Config condition(ConditionType condition) {
      this.condition = condition;
      return this;
    }

    public Config gapKey(String gapKey) {
      this.gapKey = gapKey;
      return this;
    }
  }
}
