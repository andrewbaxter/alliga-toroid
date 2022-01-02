package com.zarbosoft.alligatoroid.compiler.mortar.value.whole;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

public class ModuleIdValue implements SimpleValue, AutoGraphMixin, LeafValue {
  public final ModuleId id;

  public ModuleIdValue(ModuleId id) {
    this.id = id;
  }
}
