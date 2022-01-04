package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.FutureValue;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.CompletableFuture;

public class Import extends LanguageValue {
  public final Value spec;

  public Import(Location id, Value spec) {
    super(id, hasLowerInSubtree(spec));
    this.spec = spec;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    Value value = ectx.evaluate(this.spec);
    if (!(value instanceof ModuleIdValue)) {
      context.moduleContext.errors.add(new WrongType(location, value, "import spec"));
      return EvaluateResult.error;
    }
    CompletableFuture<Value> importResult =
        context.moduleContext.compileContext.modules.get(
            context.moduleContext,
            context.moduleContext.importPath,
            new ImportId(((ModuleIdValue) value).id));
    context.deferredErrors.add(new ROPair<>(location, importResult));
    return ectx.build(new FutureValue(importResult.exceptionally(e -> ErrorValue.error)));
  }
}
