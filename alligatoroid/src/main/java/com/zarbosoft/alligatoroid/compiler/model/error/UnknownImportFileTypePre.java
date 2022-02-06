package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.rendaw.common.Format;

public class UnknownImportFileTypePre extends Error.PreError {
  public final ModuleId id;

  public UnknownImportFileTypePre(ModuleId id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return Format.format("The file type of the module [%s} is not recognized", id);
  }
}
