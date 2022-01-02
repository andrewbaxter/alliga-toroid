package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public interface NoExportValue extends Value {
  @Override
  default boolean canExport() {
    return false;
  }

  @Override
  default Value type() {
    throw new Assertion();
  }

  @Override
  default SemiserialSubvalue graphSerialize(
      ImportId spec, Semiserializer semiserializer, ROList<Value> path, ROList<String> accessPath) {
    throw new Assertion();
  }
}
