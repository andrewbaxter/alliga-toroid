package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class EvaluateResult {
  public static final EvaluateResult error =
      new EvaluateResult(null, ErrorValue.value, ROMap.empty);
  public static final EvaluateResult unreachable =
      new EvaluateResult(null, UnreachableValue.value, ROMap.empty);
  public final TargetCode effect;

  public final Value value;
  public final ROMap<JumpKey, ROList<Jump>> jumps;

  public static class Jump {
    public final Value value;
    public final ScopeState scope;

    public Jump(Value value, ScopeState scope) {
      this.value = value;
      this.scope = scope;
    }
  }

  public EvaluateResult(
          TargetCode effect, Value value, ROMap<JumpKey, ROList<Jump>> jumps) {
    this.effect = effect;
    this.value = value;
    this.jumps = jumps;
  }

  public static <T extends Value> EvaluateResult pure(T value) {
    return new EvaluateResult(null, value, ROMap.empty);
  }

  public static <T extends Value> EvaluateResult simple(T value, TargetCode code) {
    return new EvaluateResult(code,value, ROMap.empty);
  }

  public static class Context {
    public final TSList<TargetCode> effect = new TSList<>();
    public final TSMap<JumpKey, TSList<Jump>> jumps = new TSMap<>();
    public final EvaluationContext context;
    private final Location location;

    public Context(EvaluationContext context, Location location) {
      this.context = context;
      this.location = location;
    }

    public Value evaluate(LanguageElement element) {
      return record(element.evaluate(context));
    }

    public void recordEffect(TargetCode sideEffect) {
      if (sideEffect == null) {
        return;
      }
      this.effect.add(sideEffect);
    }

    public Value record(EvaluateResult res) {
      effect.add(res.effect);
      for (Map.Entry<JumpKey, ROList<Jump>> jumpValue : res.jumps) {
        jumps.getCreate(jumpValue.getKey(), () -> new TSList<>()).addAll(jumpValue.getValue());
      }
      return res.value;
    }

    public EvaluateResult build(Value value) {
      return new EvaluateResult(
          context.target.merge(context, location, effect),
          value,
          TSMap.createWith(
              m -> {
                for (Map.Entry<JumpKey, TSList<Jump>> namedJumpValue : jumps) {
                  m.put(namedJumpValue.getKey(), namedJumpValue.getValue());
                }
              }));
    }
  }
}
