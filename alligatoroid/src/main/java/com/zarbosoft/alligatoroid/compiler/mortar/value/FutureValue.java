package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.RootExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

import java.util.concurrent.Future;

public class FutureValue implements Value {
  public final Future<Value> future;

  public FutureValue(Future<Value> future) {
    this.future = future;
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
}
