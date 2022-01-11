package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

public class ModuleIdValue implements SimpleValue, AutoGraphMixin, LeafValue {
  public final ModuleId id;

  public ModuleIdValue(ModuleId id) {
    this.id = id;
  }
}
