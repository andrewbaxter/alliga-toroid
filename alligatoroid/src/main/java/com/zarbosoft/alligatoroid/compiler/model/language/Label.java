package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.ScopeState;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;
import java.util.function.Function;

public class Label extends LanguageElement {
  @BuiltinAutoExporter.Param public String key;
  @BuiltinAutoExporter.Param public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return value.hasLowerInSubtree();
  }

  public static EvaluateResult evaluateLabeled(
      EvaluationContext context,
      Location id,
      // Nullable, for jumping 1 unlabeled block up
      String stringKey,
      Function<EvaluationContext, EvaluateResult> inner) {
    final JumpKey jumpKey = new JumpKey();
    context.scope.labels.add(new ROPair<>(stringKey, jumpKey));
    final EvaluateResult res;
    {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
      final Value res1 = ectx.record(inner.apply(context));
      res = ectx.build(ectx.record(res1.realize(context, id)));
    }

    // Locate paths leading to/out of this label and sort the values for merging
    TSList<ROPair<Location, ScopeState>> unforkScopes = new TSList<>();
    TSList<ROPair<Location, Value>> unforkValues = new TSList<>();
    TSMap<JumpKey, TSList<EvaluateResult.Jump>> forwardRemainingJumpResults = new TSMap<>();
    for (Map.Entry<JumpKey, ROList<EvaluateResult.Jump>> e : res.jumps) {
      if (e.getKey() == jumpKey) {
        for (EvaluateResult.Jump pair : e.getValue()) {
          if (pair.value == UnreachableValue.value) {
            continue;
          }
          unforkValues.add(new ROPair<>(pair.location, pair.value));
          unforkScopes.add(new ROPair<>(pair.location, pair.scope));
        }
      } else {
        forwardRemainingJumpResults.put(e.getKey(), e.getValue().mut());
      }
    }
    if (res.value != UnreachableValue.value) {
      unforkValues.add(new ROPair<>(id, res.value));
      unforkScopes.add(new ROPair<>(id, context.scope));
    }

    // Merge scopes
    Location firstScopeLocation = unforkScopes.get(0).first;
    context.scope = unforkScopes.get(0).second;
    if (unforkScopes.size() > 1) {
      for (int i = 1; i < unforkScopes.size(); ++i) {
        context.scope.merge(context, firstScopeLocation, unforkScopes.get(i));
      }
    }

    // Merge values
    unforkValues.reverse();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    ectx.recordEffect(res.effect);
    ectx.recordEffect(context.target.codeLand(jumpKey));
    ectx.jumps.putAll(forwardRemainingJumpResults);

    if (unforkValues.isEmpty()) {
      return ectx.build(UnreachableValue.value);
    } else {
      Location baseLocation = unforkValues.get(0).first;
      Value base = unforkValues.get(0).second;
      for (ROPair<Location, Value> other : unforkValues.subFrom(1)) {
        base = base.unfork(context, baseLocation, other);
      }
      return ectx.build(base);
    }
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context0) {
    return evaluateLabeled(
        context0,
        id,
        key,
        c1 -> {
          return Scope.evaluateScoped(c1, id, c2 -> value.evaluate(c2), ROOrderedMap.empty);
        });
  }
}
