package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

public class Return extends LanguageElement {
  @BuiltinAutoExportableType.Param public String key;
  @BuiltinAutoExportableType.Param public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return value.hasLowerInSubtree();
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Value res = ectx.evaluate(value);
    boolean found = false;
    JumpKey dest = null;
    while (true) {
      for (ROPair<String, JumpKey> label : context.scope.labels) {
        if (label.first.equals(key)) {
          dest = label.second;
          found = true;
          break;
        }
      }
      if (found) {
        break;
      }
      for (Binding binding : new ReverseIterable<>(context.scope.atLevel())) {
        ectx.recordEffect(binding.dropCode(context, id));
      }
      context.popScope();
    }
    if (!found) {
      if (key == null) {
        throw new Assertion();
      } else {
        context.errors.add(
            new GeneralLocationError(id, String.format("No return destination with name %s", key)));
      }
    }
    ectx.jumps
        .getCreate(dest, () -> new TSList<>())
        .add(new EvaluateResult.Jump(res, context.scope));
    ectx.recordEffect(context.target.codeJump(dest));
    return ectx.build(UnreachableValue.value);
  }
}
