package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialSubvalueExportableBuiltin implements SemiserialSubvalueExportable {
  @Artifact.Param public String key;

  public static SemiserialSubvalueExportableBuiltin create(String key) {
    final SemiserialSubvalueExportableBuiltin out = new SemiserialSubvalueExportableBuiltin();
    out.key = key;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltin(this);
  }
}
