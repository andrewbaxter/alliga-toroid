package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

public class ModLocal extends LanguageValue {
  public final Value path;

  public ModLocal(Location id, Value path) {
    super(id, hasLowerInSubtree(path));
    this.path = path;
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue path = WholeValue.getWhole(context, location, ectx.evaluate(this.path));
    if (path == null) return EvaluateResult.error;
    return ectx.build(
        new FutureValue(
            context.module.compilationContext.loadRelativeModule(
                context.module.id, (String) path.concreteValue())));
  }
}
