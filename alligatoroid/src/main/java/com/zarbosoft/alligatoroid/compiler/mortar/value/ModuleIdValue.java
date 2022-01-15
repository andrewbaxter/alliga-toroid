package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;

public class ModuleIdValue implements SimpleValue, AutoBuiltinExportable, LeafExportable {
  public final ModuleId id;

  public ModuleIdValue(ModuleId id) {
    this.id = id;
  }
}
