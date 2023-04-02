package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeJump;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.rendaw.common.ROPair;
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
    final ROPair<JumpKey, Value> jump = new ROPair<>(new JumpKey(), res);
    // TODO drop scopes up until dest
    // TODO store remaining scope tree in jumps as well as value, for merge
    if (key.isEmpty()) {
    ROList<Scope> scopes = context.popScopesUntilBlock();
      ectx.jumps.add(jump);
    } else {
      ROList<Scope> scopes = context.popScopesUntilName(key);
      ectx.namedJumps.getCreate(key, () -> new TSList<>()).add(jump);
    }
    ectx.recordPre(new MortarTargetCode(new JavaBytecodeJump(key)));
    return ectx.build(UnreachableValue.value);
  }
}
