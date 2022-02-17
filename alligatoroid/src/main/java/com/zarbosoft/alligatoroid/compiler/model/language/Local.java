package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class Local extends LanguageElement {
  @Param public LanguageElement key;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(key);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Object key = assertConstKey(context, id, ectx.evaluate(this.key));
    if (key == null) return EvaluateResult.error;
    Binding value = context.scope.get(key);
    if (value == null) {
      context.moduleContext.errors.add(new NoField(id, key));
      return EvaluateResult.error;
    }
    return ectx.build(ectx.record(value.fork(context, id)));
  }
}
