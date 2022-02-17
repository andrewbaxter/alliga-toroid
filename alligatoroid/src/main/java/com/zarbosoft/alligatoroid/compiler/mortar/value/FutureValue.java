package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleBinding;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.Future;

public class FutureValue implements Value, NoExportValue {
  public final Future<Value> future;

  public FutureValue(Future<Value> future) {
    this.future = future;
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    return EvaluateResult.pure(get());
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, new SimpleBinding(this));
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return get().drop(context, location);
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return get().vary(context, location);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return get().access(context, location, field);
  }

  public Value get() {
    return Utils.await(future);
  }

  @Override
  public ExportableType graphType() {
    return get().graphType();
  }
}
