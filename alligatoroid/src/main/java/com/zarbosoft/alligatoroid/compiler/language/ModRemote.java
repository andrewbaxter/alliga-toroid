package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

public class ModRemote extends LanguageValue {
  public final Value url;
  public final Value hash;

  public ModRemote(Location id, Value url, Value hash) {
    super(id, hasLowerInSubtree(url, hash));
    this.url = url;
    this.hash = hash;
  }

  public Object graphDeserialize(Record data) {
    return graphDeserialize(this.getClass(), data);
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue url0 = WholeValue.getWhole(context, location, ectx.evaluate(this.url));
    WholeValue hash0 = WholeValue.getWhole(context, location, ectx.evaluate(this.hash));
    if (url0 == null || hash0 == null) return EvaluateResult.error;

    return ectx.build(
        new ModuleIdValue(
            new RemoteModuleId((String) url0.concreteValue(), (String) hash0.concreteValue())));
  }
}
