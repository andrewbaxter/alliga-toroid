package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.core.syntax.symbol.Symbol;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualParent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtomFromArray;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontArrayAsAtomSpec extends FrontSpec {
  public final String fieldId;
  private final Symbol ellipsis;
  private final ROMap<String, Object> ellipsisMeta;
  private final ROSetRef<String> forwardAlignments;
  private BaseBackArraySpec field;

  public FrontArrayAsAtomSpec(Config config) {
    this.fieldId = config.field;
    ellipsis = config.ellipsis;
    ellipsisMeta = config.ellipsisMeta;
    forwardAlignments = config.forwardAlignments;
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final int visualDepth,
      final int depthScore) {
    return new VisualFieldAtomFromArray(
        context,
        parent,
        field.get(atom.namedFields),
        visualDepth,
        depthScore,
        ellipsis,
        ellipsisMeta,forwardAlignments);
  }

  @Override
  public void finish(
      MultiError errors,
      SyntaxPath typePath,
      final AtomType atomType,
      final TSSet<String> middleUsed) {
    middleUsed.add(fieldId);
    field =
        (BaseBackArraySpec)
            atomType.getDataArray(errors, typePath, fieldId, "front array-as-atom spec");
  }

  @Override
  public String fieldId() {
    return fieldId;
  }

  public static class Config {
    public final String field;
    public final Symbol ellipsis;
    public ROMap<String, Object> ellipsisMeta = ROMap.empty;
    private ROSetRef<String> forwardAlignments;

    public Config forwardAlignments(ROSetRef<String> forward) {
      this.forwardAlignments = forward;
      return this;
    }
    public Config(String field, Symbol ellipsis) {
      this.field = field;
      this.ellipsis = ellipsis;
    }

    public Config ellipsisMeta(ROMap<String, Object> ellipsisMeta) {
      this.ellipsisMeta = ellipsisMeta;
      return this;
    }
  }
}
