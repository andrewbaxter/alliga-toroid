package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface SemiserialSubvalueRef extends SemiserialSubvalue {
  public static final Class<? extends SemiserialSubvalue>[] SERIAL_UNION =
      new Class[] {
        SemiserialSubvalueRefIdentity.class, SemiserialSubvalueRefBuiltin.class,
      };

  public <T> T dispatchExportable(Dispatcher<T> dispatcher);

  @Override
  default <T> T dispatch(SemiserialSubvalue.Dispatcher<T> dispatcher) {
    return dispatcher.handleRef(this);
  }

  public interface Dispatcher<T> {
    T handleArtifact(SemiserialSubvalueRefIdentity s);

    T handleBuiltin(SemiserialSubvalueRefBuiltin s);
  }

  public interface DefaultDispatcher<T> extends Dispatcher<T> {
    @Override
    default T handleArtifact(SemiserialSubvalueRefIdentity s) {
      throw new RuntimeException("got unexpected semiserial exp ref");
    }

    @Override
    default T handleBuiltin(SemiserialSubvalueRefBuiltin s) {
      throw new RuntimeException("got unexpected semiserial exp builtin");
    }
  }
}
