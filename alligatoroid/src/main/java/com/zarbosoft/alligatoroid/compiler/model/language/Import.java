package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeOther;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.CompletableFuture;

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
    if (value == ErrorValue.error) return EvaluateResult.error;
    if (!(value instanceof WholeOther) || !(((WholeOther) value).object instanceof ModuleId)) {
      context.moduleContext.errors.add(new WrongType(location, value, "module id"));
      return EvaluateResult.error;
    }
    CompletableFuture<MortarValue> importResult =
        context.moduleContext.getModule(new ImportId((ModuleId) ((WholeOther) value).object));
    context.deferredErrors.add(new ROPair<>(location, importResult));
    return ectx.build(new FutureValue(importResult.exceptionally(e -> ErrorValue.error)));
  }
}
