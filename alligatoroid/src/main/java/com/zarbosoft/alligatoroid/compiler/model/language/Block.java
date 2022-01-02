package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class Block extends LanguageValue {
  public final ROList<Value> statements;

  public Block(Location id, ROList<Value> statements) {
    super(id, hasLowerInSubtree(statements));
    this.statements = statements;
  }

  public static EvaluateResult evaluate(
          EvaluationContext context, Location location, ROList<Value> children) {
    TSList<TargetCode> pre = new TSList<>();
    EvaluateResult lastRes = null;
    Location lastLocation = null;
    for (Value child : children) {
      if (lastRes != null) {
        pre.add(lastRes.preEffect);
        pre.add(lastRes.value.drop(context, lastLocation));
        pre.add(lastRes.postEffect);
      }
      lastLocation = child.location();
      lastRes = child.evaluate(context);
    }
    Value last;
    TargetCode post = null;
    if (lastRes != null) {
      pre.add(lastRes.preEffect);
      last = lastRes.value;
      post = lastRes.postEffect;
    } else {
      last = NullValue.value;
    }
    return new EvaluateResult(context.target.merge(context, location, pre), post, last);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return evaluate(context, location, statements);
  }
}
