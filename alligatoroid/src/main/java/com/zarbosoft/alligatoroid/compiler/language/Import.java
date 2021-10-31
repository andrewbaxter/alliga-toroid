package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.CompilationContext;
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
    CompilationContext.ImportResult importResult =
        context.module.compilationContext.loadModule(
            context.module.importPath, new ImportSpec(((ModuleIdValue) value).id));
    if (importResult.error != null) {
      context.module.log.errors.add(importResult.error.toError(location));
      return EvaluateResult.error;
    }
    return ectx.build(new FutureValue(importResult.value));
  }
}
