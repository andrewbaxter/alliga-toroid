package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;

public interface Module extends ModuleResult {
  long cacheId();

  ImportId spec();

  SemiserialModule result();
}
