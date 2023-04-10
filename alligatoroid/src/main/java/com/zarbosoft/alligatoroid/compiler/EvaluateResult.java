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
      new EvaluateResult(null, null, ErrorValue.value, ROMap.empty);
  public static final EvaluateResult unreachable =
      new EvaluateResult(null, null, UnreachableValue.value, ROMap.empty);
  /**
   * Code that results in no stack change, the effects of everything leading up to the result of an
   * expression. Includes the code that results in the stack with value on top. In case value is
   * unreachable, the pre effect should be consumed.
   */
  public final TargetCode preEffect;
  /**
   * Code that results in no stack change. Must remain separate from pre until the end of the
   * statement (code for dropping temporaries). In the case value is unreachable, post should be
   * empty.
   */
  public final TargetCode postEffect;

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
      TargetCode preEffect, TargetCode postEffect, Value value, ROMap<JumpKey, ROList<Jump>> jumps) {
    this.preEffect = preEffect;
    this.postEffect = postEffect;
    this.value = value;
    this.jumps = jumps;
  }

  public static <T extends Value> EvaluateResult pure(T value) {
    return new EvaluateResult(null, null, value, ROMap.empty);
  }

  public static class Context {
    public final TSList<TargetCode> preEffect = new TSList<>();
    public final TSList<TargetCode> postEffect = new TSList<>();
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

    public void recordPost(TargetCode sideEffect) {
      if (sideEffect == null) {
        return;
      }
      this.postEffect.add(sideEffect);
    }

    public void recordPre(TargetCode sideEffect) {
      if (sideEffect == null) {
        return;
      }
      this.preEffect.add(sideEffect);
    }

    public Value record(EvaluateResult res) {
      preEffect.add(res.preEffect);
      postEffect.add(res.postEffect);
      for (Map.Entry<JumpKey, ROList<Jump>> jumpValue : res.jumps) {
        jumps.getCreate(jumpValue.getKey(), () -> new TSList<>()).addAll(jumpValue.getValue());
      }
      return res.value;
    }

    public EvaluateResult build(Value value) {
      return new EvaluateResult(
          context.target.merge(context, location, preEffect),
          context.target.merge(context, location, new ReverseIterable<>(postEffect)),
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
