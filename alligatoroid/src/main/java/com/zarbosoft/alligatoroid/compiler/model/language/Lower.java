package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.alligatoroid.compiler.modules.modulecompiler.ModuleCompiler;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarType;
import com.zarbosoft.rendaw.common.ROPair;

public final class Lower extends LanguageElement {
  @AutoExporter.Param public LanguageElement child;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return true;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    final ROPair<MortarType, Object> evalRes =
        ModuleCompiler.rootEvaluate(context.moduleContext, child);
    return EvaluateResult.pure(evalRes.first.type_constAsValue(evalRes.second));
  }
}
