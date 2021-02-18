package com.zarbosoft.merman.syntax.front;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontArray;
import com.zarbosoft.merman.misc.MultiError;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.syntax.style.Style;
import com.zarbosoft.merman.syntax.symbol.Symbol;
import com.zarbosoft.merman.syntax.symbol.SymbolSpaceSpec;
import com.zarbosoft.merman.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.TSSet;

import static com.zarbosoft.merman.syntax.style.Style.SplitMode.NEVER;

public abstract class FrontArraySpecBase extends FrontSpec {
  public final ROList<FrontSymbol> prefix;
  public final ROList<FrontSymbol> suffix;
  public final ROList<FrontSymbol> separator;
  public final Symbol ellipsis;
  public final Symbol empty;
  public BaseBackArraySpec dataType;

  public FrontArraySpecBase(Config config) {
    this.prefix = config.prefix;
    this.suffix = config.suffix;
    this.separator = config.separator;
    this.ellipsis = config.ellipsis;
    empty = config.empty;
  }

  public BaseBackArraySpec dataType() {
    return dataType;
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final int visualDepth,
      final int depthScore) {
    VisualFrontArray out = new VisualFrontArray(this,  parent, atom, visualDepth);
    out.root(context, parent, depthScore, depthScore);
    return out;
  }

  @Override
  public void finish(
      MultiError errors, Path typePath, final AtomType atomType, final TSSet<String> middleUsed) {
    middleUsed.add(field());
    dataType = atomType.getDataArray(errors, typePath, field());
  }

  public abstract String field();

  @Override
  public void dispatch(final DispatchHandler handler) {
    handler.handle(this);
  }

  public static class Config {
    public ROSet<String> tags = ROSet.empty;
    public ROList<FrontSymbol> prefix = ROList.empty;
    public ROList<FrontSymbol> suffix = ROList.empty;
    public ROList<FrontSymbol> separator = ROList.empty;
    public boolean tagFirst = false;
    public boolean tagLast = false;
    public Symbol ellipsis = new SymbolTextSpec(new SymbolTextSpec.Config("..."));
    public Symbol empty = new SymbolSpaceSpec(new SymbolSpaceSpec.Config());

    public Config() {}
  }
}
