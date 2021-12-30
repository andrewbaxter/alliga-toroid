package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseTuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class Tuple extends LanguageValue {
  public final ROList<Value> elements;

  public Tuple(Location id, ROList<Value> elements) {
    super(id, hasLowerInSubtree(elements));
    this.elements = elements;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    TSList<EvaluateResult> data = new TSList<>();
    for (Value element : elements) {
      data.add(element.evaluate(context));
    }
    return EvaluateResult.pure(new LooseTuple(data));
  }
}
