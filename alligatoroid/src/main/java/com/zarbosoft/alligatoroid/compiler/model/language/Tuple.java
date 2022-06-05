package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class Tuple extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public ROList<LanguageElement> elements;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtreeList(elements);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    TSList<EvaluateResult> data = new TSList<>();
    for (LanguageElement element : elements) {
      data.add(element.evaluate(context));
    }
    return EvaluateResult.pure(new LooseTuple(data));
  }
}
