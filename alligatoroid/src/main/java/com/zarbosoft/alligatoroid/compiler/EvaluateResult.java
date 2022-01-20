package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

public class EvaluateResult<V extends Value> {
  public static final EvaluateResult error = new EvaluateResult(null, null, ErrorValue.error);
  /** TargetCode or null if no effect */
  public final TargetCode preEffect;

  public final TargetCode postEffect;

  public final V value;

  public EvaluateResult(TargetCode preEffect, TargetCode postEffect, V value) {
    this.preEffect = preEffect;
    this.postEffect = postEffect;
    this.value = value;
  }

  public static <T extends Value> EvaluateResult<T> pure(T value) {
    return new EvaluateResult<>(null, null, value);
  }

  public static class Context {
    public final TSList<TargetCode> preEffect = new TSList<>();
    public final TSList<TargetCode> postEffect = new TSList<>();
    public final EvaluationContext context;
    private final Location location;

    public Context(EvaluationContext context, Location location) {
      this.context = context;
      this.location = location;
    }

    public Value evaluate(LanguageElement element) {
      return record(element.evaluate(context));
    }

    public void recordPre(TargetCode sideEffect) {
      if (sideEffect == null) return;
      this.preEffect.add(sideEffect);
    }

    public Value record(EvaluateResult res) {
      preEffect.add(res.preEffect);
      postEffect.add(res.postEffect);
      return res.value;
    }

    public EvaluateResult build(Value value) {
      return new EvaluateResult(
          context.target.merge(context, location, preEffect),
          context.target.merge(context, location, new ReverseIterable<>(postEffect)),
          value);
    }
  }
}
