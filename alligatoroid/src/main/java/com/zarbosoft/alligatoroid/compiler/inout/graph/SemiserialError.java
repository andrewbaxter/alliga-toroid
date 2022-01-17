package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;

public class SemiserialError implements SemiserialSubvalue {
  public static final SemiserialError error = new SemiserialError();

  private SemiserialError() {}

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    throw new Assertion();
  }
}
