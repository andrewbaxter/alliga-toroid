package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.error.NotRecordPair;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstKey;

public class Record extends LanguageElement {
  @BuiltinAutoExportableType.Param public ROList<LanguageElement> elements;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtreeList(elements);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    TSOrderedMap<Object, EvaluateResult> data = new TSOrderedMap<>();
    boolean badKeys = false;
    for (LanguageElement element : elements) {
      if (!(element instanceof RecordElement)) {
        context.errors.add(
            new NotRecordPair(((LanguageElement) element).id, element.getClass().getSimpleName()));
        continue;
      }
      EvaluateResult keyRes = ((RecordElement) element).key.evaluate(context);
      Object key = assertConstKey(context, id, keyRes.value);
      if (key == null) {
        badKeys = true;
        continue;
      }
      EvaluateResult valueRes = ((RecordElement) element).value.evaluate(context);
      data.put(
          key,
          EvaluateResult.simple(
              valueRes.value,
              context.target.merge(context, id, new TSList<>(keyRes.effect, valueRes.effect))));
    }
    if (badKeys) {
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new LooseRecord(data));
  }
}
