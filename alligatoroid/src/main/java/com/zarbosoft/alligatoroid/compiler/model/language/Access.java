package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROList;

public class Access extends LanguageElement {
  @BuiltinAutoExporter.Param
  public LanguageElement base;
  @BuiltinAutoExporter.Param
  public LanguageElement key;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(base, key);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Value base = ectx.evaluate(this.base);
    ROList<String> stringFields = base.traceFields(context, id);
    context.moduleContext.compileContext.addTraceModuleStringFields(
        context.moduleContext.importId.moduleId, id, stringFields.toSet());
    return ectx.build(ectx.record(base.access(context, id, ectx.evaluate(this.key))));
  }
}
