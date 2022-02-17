package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.CompletableFuture;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstModuleId;

public class Import extends LanguageElement {
  @Param public LanguageElement spec;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(spec);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    Value value = ectx.evaluate(this.spec);
    ModuleId id = assertConstModuleId(context, this.id, value);
    if (id == null) return EvaluateResult.error;
    CompletableFuture<Value> importResult = context.moduleContext.getModule(ImportId.create(id));
    context.deferredErrors.add(new ROPair<>(this.id, importResult));
    return ectx.build(new FutureValue(importResult.exceptionally(e -> {
      return ErrorValue.error;
    })));
  }
}
