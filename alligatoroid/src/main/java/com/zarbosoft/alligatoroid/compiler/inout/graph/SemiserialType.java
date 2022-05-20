package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;

public class SemiserialType implements SemiserialSubvalue {
  @AutoBuiltinExportableType.Param
  public String type;
  @AutoBuiltinExportableType.Param
  public SemiserialSubvalue value;

  public static SemiserialType create(String type, SemiserialSubvalue value) {
    final SemiserialType out = new SemiserialType();
    out.type = type;
    out.value = value;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleType(this);
  }
}
