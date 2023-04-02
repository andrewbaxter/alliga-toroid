package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTupleTypestate;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstIntlike;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst.nullValue;

/**
 * Represents consecutive stack elements - needs to be converted to an actual tuple to bind/access
 * (TODO conversion)
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
  public EvaluateResult vary(EvaluationContext context, Location id) {
    TODO();
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final Integer key = assertConstIntlike(context, location, field);
    if (key == null) {
      return EvaluateResult.error;
    }
    TSList<TargetCode> pre = new TSList<>();
    TSList<TargetCode> post = new TSList<>();
    Value out = null;
    for (int i = 0; i < data.size(); ++i) {
      EvaluateResult e = data.get(i);
      if (out == null) {
        pre.add(e.preEffect);
        if (key == i) {
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
      context.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return new EvaluateResult(
        context.target.merge(context, location, pre),
        context.target.merge(context, location, post),
        out, jumpValues, jumpValues);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    for (EvaluateResult value : new ReverseIterable<>(data)) {
      ectx.recordPre(ectx.record(value).drop(context, location));
    }
    return ectx.build(nullValue).preEffect;
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    TSList<MortarDataTypestate> types = new TSList<>();
    final TSList<Object> data = new TSList<>();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    for (int i = 0; i < this.data.size(); i++) {
      Value exported = ectx.record(ectx.record(this.data.get(i)).export(context, location));
      if (!(exported instanceof MortarDataValueConst)) {
        throw new Assertion();
      }
      types.add(((MortarDataValueConst) exported).type());
      data.add(((MortarDataValueConst) exported).getInner());
    }
    return ectx.build(new MortarTupleTypestate(types).typestate_constAsValue(Tuple.create(data)));
  }
}
