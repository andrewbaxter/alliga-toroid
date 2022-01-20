package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

public class Scope extends LanguageElement {
  public  LanguageElement inner;

  public Scope(Location id, LanguageElement inner) {
    super(id, hasLowerInSubtree(inner));
    this.inner = inner;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    context.pushScope();
    EvaluateResult res = inner.evaluate(context);
    TSList<TargetCode> pre = new TSList<>(res.preEffect);
    for (Binding binding : new ReverseIterable<>(context.scope.atLevel())) {
      pre.add(context.target.drop(context, location, binding));
    }
    context.popScope();
    return new EvaluateResult(
        context.target.merge(context, location, pre), res.postEffect, res.value);
  }
}
