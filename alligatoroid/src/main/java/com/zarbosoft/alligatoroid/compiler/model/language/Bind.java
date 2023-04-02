package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstKey;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class Bind extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public LanguageElement key;
  @BuiltinAutoExportableType.Param
  public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(key, value);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Object key = assertConstKey(context, id, ectx.evaluate(this.key));
    Value value = ectx.evaluate(this.value);
    if (key == null) {
      context.scope.error = true;
      return EvaluateResult.error;
    }
    Binding old = context.scope.remove(key);
    if (old != null) {
      ectx.recordPre(old.dropCode(context, id));
    }
    ROPair<TargetCode, Binding> bound = value.bind(context, id);
    context.scope.put(key, bound.second);
    ectx.recordPre(bound.first);
    return ectx.build(nullValue);
  }
}
