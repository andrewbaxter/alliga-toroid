package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;

public interface PrimitiveExportType {
  String key();

  Object desemiserialize(SemiserialSubvalue data);

  SemiserialSubvalue semiserialize(Object data);
}
