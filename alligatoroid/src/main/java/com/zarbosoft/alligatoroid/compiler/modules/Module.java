package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;

public interface Module extends ModuleResult{
  ImportId spec();

  SemiserialModule result();
}
