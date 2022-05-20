package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportableType;

/** A single instance exists, exportable looks it up by class name turned into a key. */
public interface SingletonBuiltinExportable extends IdentityExportableType {
  @Override
  default ExportableType exportableType() {
    return Meta.singletonBuiltinExportableTypes.get(this);
  }
}
