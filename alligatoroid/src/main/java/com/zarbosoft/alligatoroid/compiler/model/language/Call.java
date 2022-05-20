package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Call extends LanguageElement {
  @AutoBuiltinExportableType.Param
  public LanguageElement target;
  @AutoBuiltinExportableType.Param
  public LanguageElement argument;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(target, argument);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    return ectx.build(
        ectx.record(ectx.evaluate(target).call(context, id, ectx.evaluate(argument))));
  }
}
