package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

public class Vary extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public LanguageElement child;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(child);
  }

  @Override
  public <V extends Value> EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    return ectx.build(ectx.record(context.target.vary(context, id, ectx.evaluate(this.child))));
  }
}
