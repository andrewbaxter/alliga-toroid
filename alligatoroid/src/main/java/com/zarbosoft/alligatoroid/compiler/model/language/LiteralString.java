package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStringType;

public class LiteralString extends LanguageElement {
  @AutoBuiltinExportableType.Param
  public String value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return false;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(MortarStringType.type.constAsValue(value));
  }
}
