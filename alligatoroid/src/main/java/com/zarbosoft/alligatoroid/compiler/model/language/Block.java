package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;

public class Block extends LanguageElement {
  @BuiltinAutoExportableType.Param public ROList<LanguageElement> statements;

  public static Block create(Location id, ROList<LanguageElement> statements) {
    final Block block = new Block();
    block.id = id;
    block.statements = statements;
    block.postInit();
    return block;
  }

  public static EvaluateResult evaluateRaw(
      EvaluationContext context, Location location, ROList<LanguageElement> children) {
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    boolean unreachable = false;
    for (LanguageElement child : children) {
      final EvaluateResult childRes = child.evaluate(context);
      ectx.recordPre(childRes.preEffect);
      if (childRes.value == UnreachableValue.value) {
        if (!context.target.isCodeEmpty(childRes.postEffect)) {
          throw new Assertion();
        }
        break;
      }
      ectx.recordPre(childRes.value.drop(context, location));
      ectx.recordPre(childRes.postEffect);
    }
    if (unreachable) {
      return ectx.build(UnreachableValue.value);
    } else {
      return ectx.build(NullValue.value);
    }
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtreeList(statements);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return Label.evaluateRaw(
        context,
        id,
        null,
        c1 -> {
          return Scope.evaluateRaw(c1, id, c2 -> evaluateRaw(c2, id, statements), ROOrderedMap.empty);
        });
  }
}
