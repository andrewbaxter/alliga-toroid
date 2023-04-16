package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstKey;

public class LooseRecord implements Value, NoExportValue {
  public final ROOrderedMap<Object, EvaluateResult> data;

  public LooseRecord(ROOrderedMap<Object, EvaluateResult> data) {
    this.data = data;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, EvaluateResult> datum : data) {
      if (!(datum.first instanceof String)) {
          continue;
      }
      out.add((String) datum.first);
    }
    return out;
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
  return context.target.realizeRecord(context, id, this);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    TSList<TargetCode> out = new TSList<>();
    for (ROPair<Object, EvaluateResult> e : data) {
      out.add(e.second.effect);
      out.add(e.second.value.drop(context, location));
    }
    return context.target.merge(context, location, out);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final Object key = assertConstKey(context, location, field);
    if (key == null) {
        return EvaluateResult.error;
    }
    TSList<TargetCode> code = new TSList<>();
    Value out = null;
    for (ROPair<Object, EvaluateResult> e : data) {
        code.add(e.second.effect);
        if (e.first.equals(key)) {
          out = e.second.value;
        } else {
          code.add(e.second.value.drop(context, location));
        }
    }
    if (out == null) {
      context.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.simple(
    out,
        context.target.merge(context, location, code)
        );
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
  throw new Assertion(); // Should be realized
  }
}
