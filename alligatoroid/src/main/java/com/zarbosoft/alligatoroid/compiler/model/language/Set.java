package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Set extends LanguageElement {
  @AutoBuiltinExportableType.Param
  public LanguageElement target;
  @AutoBuiltinExportableType.Param
  public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(target, value);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    Value target = ectx.evaluate(this.target);
    Value value = ectx.evaluate(this.value);
    return target.set(context, id, value);
  }
}
