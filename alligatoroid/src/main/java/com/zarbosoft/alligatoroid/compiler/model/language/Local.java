package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTupleTypestate.assertConstKey;

public class Local extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public LanguageElement key;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(key);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    TSList<String> stringFields = new TSList<>();
    for (ROPair<Object, Binding> pair : context.scope.keys()) {
      if (!(pair.first instanceof String)) {
          continue;
      }
      stringFields.add((String) pair.first);
    }
    context.moduleContext.compileContext.addTraceModuleStringFields(
        context.moduleContext.importId.moduleId, id, stringFields.toSet());

    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Object key = assertConstKey(context, id, ectx.evaluate(this.key));
    if (key == null) {
        return EvaluateResult.error;
    }
    Binding value = context.scope.get(key);
    if (value == null) {
      context.errors.add(new NoField(id, key));
      return EvaluateResult.error;
    }
    return ectx.build(ectx.record(value.load(context, id)));
  }
}
