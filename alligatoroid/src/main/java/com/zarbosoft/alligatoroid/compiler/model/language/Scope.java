package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

public class Scope extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public LanguageElement inner;

  public static Scope create(Location id, LanguageElement inner) {
    final Scope scope = new Scope();
    scope.id = id;
    scope.inner = inner;
    scope.postInit();
    return scope;
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(inner);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    context.pushScope();
    EvaluateResult res = inner.evaluate(context);
    TSList<TargetCode> pre = new TSList<>(res.preEffect);
    for (Binding binding : new ReverseIterable<>(context.scope.atLevel())) {
      pre.add(binding.dropCode(context, id));
    }
    context.popScope();
    return new EvaluateResult(
        context.target.merge(context, id, pre), res.postEffect, res.value);
  }
}
