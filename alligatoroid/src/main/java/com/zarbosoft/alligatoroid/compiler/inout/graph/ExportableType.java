package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;

public interface ExportableType extends Exportable {
  SemiserialRef graphSemiserialize(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath);

  Object graphDesemiserialize(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialRef data);
}
