package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter;
import com.zarbosoft.rendaw.common.Assertion;

public interface NoExportValue extends Exportable {
  @Override
  default void postInit() {
    throw new Assertion();
  }

  @Override
  default Exporter exporter() {
    throw new Assertion();
  }
}
