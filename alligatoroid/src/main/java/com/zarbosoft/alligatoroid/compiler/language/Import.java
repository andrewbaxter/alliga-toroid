package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.concurrent.CompletableFuture;

public class Import extends LanguageValue {
  public final Value spec;

  public Import(Location id, Value spec) {
    super(id, hasLowerInSubtree(spec));
    this.spec = spec;
  }

  public Object graphDeserialize(Record data) {
    return graphDeserialize(this.getClass(), data);
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    Value value = ectx.evaluate(this.spec);
    if (!(value instanceof ModuleIdValue)) {
      context.module.log.errors.add(new Error.WrongType(location, value, "import spec"));
      return EvaluateResult.error;
    }
    CompletableFuture<Value> importResult =
        context.module.compilationContext.loadModule(
            new ROPair<>(location, context.module), context.module.importPath, new ImportSpec(((ModuleIdValue) value).id));
    return ectx.build(new FutureValue(importResult));
  }
}
