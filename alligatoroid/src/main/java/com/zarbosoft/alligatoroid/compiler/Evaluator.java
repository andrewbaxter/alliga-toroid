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
  /**
   * Evaluates a tree, producing target code + result value
   *
   * @param moduleContext
   * @param targetContext
   * @param isModuleRoot
   * @param language
   * @param initialScope
   * @return
   */
  public static RootEvaluateResult evaluate(
      ModuleCompileContext moduleContext,
      TargetModuleContext targetContext,
      boolean isModuleRoot,
      LanguageElement language,
      ROOrderedMap<Object, Binding> initialScope) {
    EvaluationContext context =
        new EvaluationContext(moduleContext, targetContext, isModuleRoot, new Scope(null));
    Location rootLocation = null;
    for (ROPair<Object, Binding> local : initialScope) {
      context.scope.put(local.first, local.second);
    }
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
    return new RootEvaluateResult(
        targetContext.merge(context, null, code),
        res.value,
        context.errors,
        context.sourceMapReverse,
        context.log);
  }

  public static class RootEvaluateResult {
    public final TargetCode code;
    public final Value value;
    public final TSList<Location> sourceMap;
    public final ROList<Error> errors;
    public final ROList<String> log;

    public RootEvaluateResult(
        TargetCode code,
        Value value,
        ROList<Error> errors,
        TSList<Location> sourceMap,
        ROList<String> log) {
      this.code = code;
      this.value = value;
      this.errors = errors;
      this.sourceMap = sourceMap;
      this.log = log;
    }
  }
}
