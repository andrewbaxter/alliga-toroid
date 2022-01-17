package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.rendaw.common.Assertion;

public interface RootExportable extends Exportable {
  @Override
  default Exportable type() {
    throw new Assertion();
  }

  @Override
  default void postInit() {}
}
