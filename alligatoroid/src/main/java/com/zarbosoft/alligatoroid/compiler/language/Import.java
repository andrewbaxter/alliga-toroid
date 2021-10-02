package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.CompilationContext;
import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ImportSpecValue;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;

public class Import extends LanguageValue {
  public final Value spec;

  public Import(Location id, Value spec) {
    super(id, hasLowerInSubtree(spec));
    this.spec = spec;
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    Value value = ectx.evaluate(this.spec);
    if (!(value instanceof ImportSpecValue)) {
      context.module.log.errors.add(Error.wrongType(location, value, "import spec"));
      return EvaluateResult.error;
    }
    CompilationContext.ImportResult importResult =
        context.module.compilationContext.loadModule(
            context.module.importPath, ((ImportSpecValue) value).spec);
    if (importResult.error != null) {
      context.module.log.errors.add(importResult.error.toError(location));
      return EvaluateResult.error;
    }
    return ectx.build(new FutureValue(importResult.value));
  }
}
