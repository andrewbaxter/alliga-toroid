package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class LooseRecord implements Value, NoExportValue {
  public final ROOrderedMap<Object, EvaluateResult> data;

  public LooseRecord(ROOrderedMap<Object, EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, EvaluateResult> datum : data) {
      if (!(datum.first instanceof String)) continue;
      out.add((String) datum.first);
    }
    return out;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    TSList<TargetCode> out = new TSList<>();
    for (ROPair<Object, EvaluateResult> e : data) {
      out.add(e.second.preEffect);
      out.add(e.second.value.drop(context, location));
      out.add(e.second.postEffect);
    }
    return context.target.merge(context, location, out);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final Object key = assertConstKey(context, location, field);
    if (key == null) return EvaluateResult.error;
    TSList<TargetCode> pre = new TSList<>();
    TSList<TargetCode> post = new TSList<>();
    Value out = null;
    for (ROPair<Object, EvaluateResult> e : data) {
      if (out == null) {
        pre.add(e.second.preEffect);
        if (e.first.equals(key)) {
          out = e.second.value;
          post.add(e.second.postEffect);
        } else {
          pre.add(e.second.value.drop(context, location));
          pre.add(e.second.postEffect);
        }
      } else {
        post.add(e.second.preEffect);
        post.add(e.second.value.drop(context, location));
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

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    TSOrderedMap<Object, MortarDataType> types = new TSOrderedMap();
    final TSMap<Object, Object> data = new TSMap<>();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    for (ROPair<Object, EvaluateResult> e : this.data) {
      Value exported = ectx.record(ectx.record(e.second).export(context, location));
      if (!(exported instanceof ConstDataValue)) throw new Assertion();
      types.put(e.first, ((ConstDataValue) exported).mortarType());
      data.put(e.first, ((ConstDataValue) exported).getInner());
    }
    return ectx.build(new MortarRecordType(types).constAsValue(Record.create(data)));
  }
}
