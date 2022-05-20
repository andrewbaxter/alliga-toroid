package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;

public class SemiserialSubvalueRefBuiltin implements SemiserialSubvalueRef {
  @AutoBuiltinExportableType.Param
  public String key;

  public static SemiserialSubvalueRefBuiltin create(String key) {
    final SemiserialSubvalueRefBuiltin out = new SemiserialSubvalueRefBuiltin();
    out.key = key;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltin(this);
  }
}
