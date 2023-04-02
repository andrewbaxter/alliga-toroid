package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarPrimitiveAll.typeString;

public class LiteralString extends LanguageElement {
  @BuiltinAutoExportableType.Param public String value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return false;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return EvaluateResult.pure(typeString.type_constAsValue(value));
  }
}
