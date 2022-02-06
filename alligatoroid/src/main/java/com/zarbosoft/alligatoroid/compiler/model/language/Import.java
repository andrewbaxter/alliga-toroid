package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.CompletableFuture;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstModuleId;

public class Import extends LanguageElement {
  public LanguageElement spec;

  public Import(Location id, LanguageElement spec) {
    super(id, hasLowerInSubtree(spec));
    this.spec = spec;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    Value value = ectx.evaluate(this.spec);
    ModuleId id = assertConstModuleId(context, location, value);
    if (id == null) return EvaluateResult.error;
    CompletableFuture<Value> importResult = context.moduleContext.getModule(new ImportId(id));
    context.deferredErrors.add(new ROPair<>(location, importResult));
    return ectx.build(new FutureValue(importResult.exceptionally(e -> ErrorValue.error)));
  }
}
