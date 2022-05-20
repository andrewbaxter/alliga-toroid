package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.Assertion;

public class RecordElement extends LanguageElement {
  @AutoBuiltinExportableType.Param
  public LanguageElement key;
  @AutoBuiltinExportableType.Param
  public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(key, value);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    // Evaluated by record literal directly
    throw new Assertion();
  }
}
