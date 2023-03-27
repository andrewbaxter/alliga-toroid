package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

public class Evaluator {
  /** Evaluates a tree, producing target code + result value */
  public static RootEvaluateResult evaluate(EvaluationContext context, LanguageElement language) {
    Location rootLocation = null;
    TSList<TargetCode> code = new TSList<>();
    final EvaluateResult res = language.evaluate(context);
    if (res == EvaluateResult.error) {
      return null;
    }
    code.add(res.preEffect);
    code.add(res.value.consume(context, rootLocation));
    for (Binding binding : new ReverseIterable<>(context.scope.atLevel())) {
      code.add(binding.dropCode(context, rootLocation));
    }
    code.add(res.postEffect);
    return new RootEvaluateResult(context.target.merge(context, null, code), res.value);
  }

  public static class RootEvaluateResult {
    public final TargetCode code;
    public final Value value;

    public RootEvaluateResult(TargetCode code, Value value) {
      this.code = code;
      this.value = value;
    }
  }
}
