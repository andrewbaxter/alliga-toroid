package com.zarbosoft.alligatoroid.compiler.mortar.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.OkValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ReverseIterable;

/**
 * Represents consecutive stack elements - needs to be converted to an actual tuple to bind/access
 * (TODO conversion)
 */
public class LooseTuple implements OkValue, LeafValue, AutoGraphMixin {
  public final ROList<EvaluateResult> data;

  public LooseTuple(ROList<EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    for (EvaluateResult value : new ReverseIterable<>(data)) {
      ectx.recordPre(ectx.record(value).drop(context, location));
    }
    return ectx.build(NullValue.value).preEffect;
  }
}
