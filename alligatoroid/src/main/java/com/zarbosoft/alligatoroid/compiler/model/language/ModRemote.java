package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;

public class ModRemote extends LanguageValue {
  public final Value url;
  public final Value hash;

  public ModRemote(Location id, Value url, Value hash) {
    super(id, hasLowerInSubtree(url, hash));
    this.url = url;
    this.hash = hash;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue url0 = WholeValue.getWhole(context, location, ectx.evaluate(this.url));
    WholeValue hash0 = WholeValue.getWhole(context, location, ectx.evaluate(this.hash));
    if (url0 == null || hash0 == null) return EvaluateResult.error;

    return ectx.build(
        new ModuleIdValue(
            new RemoteModuleId((String) url0.concreteValue(), (String) hash0.concreteValue())));
  }
}
