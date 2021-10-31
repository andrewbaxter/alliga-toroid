package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.back.BackAtomSpec;
import com.zarbosoft.merman.core.syntax.symbol.Symbol;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualParent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtom;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontAtomSpec extends FrontSpec {
  private final String fieldId;
  private final Symbol ellipsis;
  private final ROMap<String, Object> ellipsisMeta;
  private BackAtomSpec field;

  public FrontAtomSpec(Config config) {
    fieldId = config.fieldId;
    ellipsis = config.ellipsis;
    ellipsisMeta = config.ellipsisMeta;
  }

  public BackAtomSpec field() {
    return field;
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final int visualDepth,
      final int depthScore) {
    return new VisualFieldAtom(
        context,
        parent,
        field.get(atom.namedFields),
        visualDepth,
        depthScore,
        ellipsis,
        ellipsisMeta);
  }

  @Override
  public void finish(
      MultiError errors,
      SyntaxPath typePath,
      final AtomType atomType,
      final TSSet<String> middleUsed) {
    middleUsed.add(fieldId);
    field = atomType.getDataAtom(errors, typePath, fieldId, "front atom spec");
  }

  @Override
  public String fieldId() {
    return fieldId;
  }

  public static class Config {
    public final String fieldId;
    public Symbol ellipsis = new SymbolTextSpec(new SymbolTextSpec.Config("..."));
    public ROMap<String, Object> ellipsisMeta = ROMap.empty;

    public Config(String fieldId) {
      this.fieldId = fieldId;
    }

    public Config ellipsisMeta(ROMap<String, Object> ellipsisMeta) {
      this.ellipsisMeta = ellipsisMeta;
      return this;
    }
  }
}
