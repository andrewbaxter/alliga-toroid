package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class LooseRecord implements OkValue, NoExportValue, Exportable {
  public final ROOrderedMap<Object, EvaluateResult> data;

  public LooseRecord(ROOrderedMap<Object, EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    TSList<TargetCode> out = new TSList<>();
    for (ROPair<Object, EvaluateResult> e : data) {
      out.add(e.second.preEffect);
      out.add(context.target.drop(context, location, e.second.value));
      out.add(e.second.postEffect);
    }
    return context.target.merge(context, location, out);
  }

  @Override
  public EvaluateResult mortarAccess(
      EvaluationContext context, Location location, MortarValue key0) {
    WholeValue key = WholeValue.getWhole(context, location, key0);
    if (key == null) return EvaluateResult.error;
    TSList<TargetCode> pre = new TSList<>();
    TSList<TargetCode> post = new TSList<>();
    Value out = null;
    for (ROPair<Object, EvaluateResult> e : data) {
      if (out == null) {
        pre.add(e.second.preEffect);
        if (e.first.equals(key.concreteValue())) {
          out = e.second.value;
          post.add(e.second.postEffect);
        } else {
          pre.add(context.target.drop(context, location, e.second.value));
          pre.add(e.second.postEffect);
        }
      } else {
        post.add(e.second.preEffect);
        post.add(context.target.drop(context, location, e.second.value));
        post.add(e.second.postEffect);
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
}
