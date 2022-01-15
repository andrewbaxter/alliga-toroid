package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public interface BuiltinExportableType extends Exportable {
  @Override
  public default SemiserialSubvalue graphSemiserialize(
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return new SemiserialRefBuiltin(getClass().getCanonicalName());
  }

  @Override
  public default void postDesemiserialize() {
    throw new Assertion();
  }

  @Override
  public default Exportable type() {
    throw new Assertion();
  }
}
