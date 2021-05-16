package com.zarbosoft.merman.core.visual.visuals;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldAtom;
import com.zarbosoft.merman.core.syntax.symbol.Symbol;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualParent;

public class VisualFieldAtom extends VisualFieldAtomBase {
  public final FieldAtom value;
  private final FieldAtom.Listener dataListener;

  public VisualFieldAtom(
      final Context context,
      final VisualParent parent,
      final FieldAtom value,
      final int visualDepth,
      final int depthScore,
      Symbol ellipsis) {
    super(visualDepth, ellipsis);
    this.value = value;
    dataListener =
        new FieldAtom.Listener() {
          @Override
          public void set(final Context context, final Atom atom) {
            VisualFieldAtom.this.set(context, atom);
          }
        };
    value.addListener(dataListener);
    value.visual = this;
    root(context, parent, visualDepth, depthScore);
  }

  @Override
  public void dispatch(VisualNestedDispatcher dispatcher) {
    dispatcher.handle((VisualFieldAtom) this);
  }

  @Override
  public Atom atomGet() {
    return value.get();
  }

  @Override
  public String nodeType() {
    return value.back().type;
  }

  @Override
  protected Field value() {
    return value;
  }

  @Override
  public String backId() {
    return value.back().id;
  }

  @Override
  protected SyntaxPath getBackPath() {
    return value.getSyntaxPath();
  }

  @Override
  public void uproot(final Context context, final Visual root) {
    value.removeListener(dataListener);
    value.visual = null;
    super.uproot(context, root);
  }

}
