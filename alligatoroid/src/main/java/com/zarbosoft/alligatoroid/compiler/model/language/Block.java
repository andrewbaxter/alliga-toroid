package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class Block extends LanguageElement {
  @BuiltinAutoExportableType.Param
  public ROList<LanguageElement> statements;

  public static Block create(Location id, ROList<LanguageElement> statements) {
    final Block block = new Block();
    block.id = id;
    block.statements = statements;
    block.postInit();
    return block;
  }

  public static EvaluateResult evaluate(
      EvaluationContext context, Location location, ROList<LanguageElement> children) {
    TSList<TargetCode> pre = new TSList<>();
    EvaluateResult lastRes = null;
    Location lastLocation = null;
    for (LanguageElement child : children) {
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
      last = nullValue;
    }
    return new EvaluateResult(context.target.merge(context, location, pre), post, last);
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtreeList(statements);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return evaluate(context, id, statements);
  }
}
