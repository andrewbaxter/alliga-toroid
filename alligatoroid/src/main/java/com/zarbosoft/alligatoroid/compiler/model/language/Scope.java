package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;

import java.util.function.Function;

public class Scope extends LanguageElement {
  @BuiltinAutoExporter.Param public LanguageElement inner;

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

  public static EvaluateResult evaluateScoped(
      EvaluationContext context,
      Location location,
      Function<EvaluationContext, EvaluateResult> inner,
      ROOrderedMap<Object, Binding> injectScope) {
    context.pushScope();
    for (ROPair<Object, Binding> local : injectScope) {
      context.scope.put(local.first, local.second);
    }
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value res = ectx.record(inner.apply(context));
    if (res != UnreachableValue.value) {
      for (Binding binding : new ReverseIterable<>(context.scope.atLevel())) {
        ectx.recordEffect(binding.dropCode(context, location));
      }
    }
    context.popScope();
    return ectx.build(res);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return evaluateScoped(context, id, c -> inner.evaluate(c), ROOrderedMap.empty);
  }
}
