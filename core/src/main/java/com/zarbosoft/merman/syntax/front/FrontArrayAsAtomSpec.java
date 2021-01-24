package com.zarbosoft.merman.syntax.front;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontAtomFromArray;
import com.zarbosoft.merman.misc.MultiError;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.syntax.symbol.Symbol;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontArrayAsAtomSpec extends FrontSpec {
  public final String field;
  private BaseBackArraySpec dataType;

  public static class Config {
    public final String field;
    public final ROSet<String> tags;

    public Config(String field, ROSet<String> tags) {
      this.field = field;
      this.tags = tags;
    }
  }

  public FrontArrayAsAtomSpec(Config config) {
    super(config.tags);
    this.field = config.field;
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final ROMap<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    return new VisualFrontAtomFromArray(
        context, parent, dataType.get(atom.fields), alignments, visualDepth, depthScore) {

      @Override
      public String nodeType() {
        return dataType.elementAtomType();
      }

      @Override
      protected Symbol ellipsis() {
        return null;
      }
    };
  }

  @Override
  public void finish(
      MultiError errors, Path typePath, final AtomType atomType, final TSSet<String> middleUsed) {
    middleUsed.add(field);
    dataType = (BaseBackArraySpec) atomType.getDataArray(errors, typePath, field);
  }

  @Override
  public String field() {
    return field;
  }

  @Override
  public void dispatch(final DispatchHandler handler) {}
}
