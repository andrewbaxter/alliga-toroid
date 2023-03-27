package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarPrimitivePrototype;

public class LiteralBool extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public boolean value;

  @Override
  protected boolean innerHasLowerInSubtree() {
  return false;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(MortarPrimitivePrototype.bytePrototype.prototype_constAsValue(value));
  }
}
