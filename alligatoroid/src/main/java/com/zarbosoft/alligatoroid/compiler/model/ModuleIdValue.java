package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

public class ModuleIdValue implements SimpleValue {
  public final ModuleId id;

  public ModuleIdValue(ModuleId id) {
    this.id = id;
  }
}
