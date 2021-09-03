package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.OkValue;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;

import java.util.concurrent.Future;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class FutureValue implements SimpleValue {
  public final Future<Value> future;

  public FutureValue(Future<Value> future) {
    this.future = future;
  }

  @Override
  public TargetCode drop(Context context, Location location) {
    return get().drop(context, location);
  }

  @Override
  public EvaluateResult access(Context context, Location location, Value field) {
    return get().access(context, location, field);
  }

  private Value get() {
    Value res = uncheck(() -> future.get());
    if (res == null) return ErrorValue.error;
    return res;
  }
}
