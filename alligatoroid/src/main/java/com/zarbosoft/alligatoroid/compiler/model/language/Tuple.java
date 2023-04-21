package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class Tuple extends LanguageElement {
  @BuiltinAutoExporter.Param
  public ROList<LanguageElement> elements;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtreeList(elements);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    TSOrderedMap<Object, EvaluateResult> data = new TSOrderedMap<>();
    for (int i = 0; i < elements.size(); i++) {
      data.put(i, elements.get(i).evaluate(context));
    }
    return EvaluateResult.pure(new LooseRecord(data));
  }
}
