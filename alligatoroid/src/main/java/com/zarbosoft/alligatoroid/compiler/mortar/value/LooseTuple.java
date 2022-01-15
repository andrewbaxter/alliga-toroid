package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.error.IndexNotInteger;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

/**
 * Represents consecutive stack elements - needs to be converted to an actual tuple to bind/access
 * (TODO conversion)
 */
public class LooseTuple implements OkValue, LeafExportable, NoExportValue {
  public final ROList<EvaluateResult> data;

  public LooseTuple(ROList<EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value key0) {
    WholeValue key = WholeValue.getWhole(context, location, key0);
    if (key == null) return EvaluateResult.error;
    Integer index =
        key.dispatch(
            new WholeValue.DefaultDispatcher<>(null) {
              @Override
              public Integer handleInt(WholeInt value) {
                return value.value;
              }
            });
    if (index == null) {
      context.moduleContext.errors.add(new IndexNotInteger(location, key0));
      return EvaluateResult.error;
    }
    TSList<TargetCode> pre = new TSList<>();
    TSList<TargetCode> post = new TSList<>();
    Value out = null;
    for (int i = 0; i < data.size(); ++i) {
      EvaluateResult e = data.get(i);
      if (out == null) {
        pre.add(e.preEffect);
        if (index == i) {
          out = e.value;
          post.add(e.postEffect);
        } else {
          pre.add(e.value.drop(context, location));
          pre.add(e.postEffect);
        }
      } else {
        post.add(e.preEffect);
        post.add(e.value.drop(context, location));
        post.add(e.postEffect);
      }
    }
    if (out == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return new EvaluateResult(
        context.target.merge(context, location, pre),
        context.target.merge(context, location, post),
        out);
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
