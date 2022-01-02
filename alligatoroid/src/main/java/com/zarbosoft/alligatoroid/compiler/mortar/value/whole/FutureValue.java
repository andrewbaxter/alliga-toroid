package com.zarbosoft.alligatoroid.compiler.mortar.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.rendaw.common.ROList;

import java.util.concurrent.Future;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class FutureValue implements SimpleValue {
  public final Future<Value> future;

  public FutureValue(Future<Value> future) {
    this.future = future;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return get().drop(context, location);
  }

  @Override
  public Value type() {
    return get().type();
  }

  @Override
  public boolean canExport() {
    return get().canExport();
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return get().access(context, location, field);
  }

  public Value get() {
    return uncheck(() -> future.get());
  }

  @Override
  public SemiserialSubvalue graphSerialize(
      ImportId spec, Semiserializer semiserializer, ROList<Value> path, ROList<String> accessPath) {
    return get().graphSerialize(spec, semiserializer, path, accessPath);
  }

  @Override
  public Value graphDeserializeValue(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    return get().graphDeserializeValue(context, typeDesemiserializer, data);
  }
}
