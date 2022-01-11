package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;

public class ModRemote extends LanguageElement {
  public final LanguageElement url;
  public final LanguageElement hash;

  public ModRemote(Location id, LanguageElement url, LanguageElement hash) {
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
