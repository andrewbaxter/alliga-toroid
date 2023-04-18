package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTupleTypestate.assertConstIntlike;

/**
 * Represents consecutive stack elements - needs to be converted to an actual tuple to bind/access
 */
public class LooseTuple implements Value, NoExportValue {
  public final ROList<EvaluateResult> data;

  public LooseTuple(ROList<EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (int i = 0; i < data.size(); i++) {
      out.add(Integer.toString(i));
    }
    return out;
  }

  @Override
  public boolean canCastTo(EvaluationContext context, AlligatorusType type) {
    return context.target.looseTupleCanCastTo(context, this, type);
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
    return context.target.looseTupleCastTo(context, location, type);
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return context.target.realizeTuple(context, id, this);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final Integer key = assertConstIntlike(context, location, field);
    if (key == null) {
      return EvaluateResult.error;
    }
    TSList<TargetCode> code = new TSList<>();
    Value out = null;
    for (int i = 0; i < data.size(); ++i) {
      EvaluateResult e = data.get(i);
      code.add(e.effect);
      if (key == i) {
        out = e.value;
      } else {
        code.add(e.value.drop(context, location));
      }
    }
    if (out == null) {
      context.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.simple(out, context.target.merge(context, location, code));
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    TSList<TargetCode> out = new TSList<>();
    for (EvaluateResult e : data) {
      out.add(e.effect);
      out.add(e.value.drop(context, location));
    }
    return context.target.merge(context, location, out);
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    throw new Assertion(); // Should be realized
  }
}
