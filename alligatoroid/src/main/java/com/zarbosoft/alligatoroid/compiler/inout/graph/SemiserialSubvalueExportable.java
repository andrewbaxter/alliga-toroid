package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface SemiserialSubvalueExportable extends SemiserialSubvalue {
  public static final Class<? extends SemiserialSubvalue>[] SERIAL_UNION =
      new Class[] {
        SemiserialSubvalueExportableIdentityRef.class, SemiserialSubvalueExportableBuiltin.class,
      };

  public <T> T dispatchExportable(Dispatcher<T> dispatcher);

  @Override
  default <T> T dispatch(SemiserialSubvalue.Dispatcher<T> dispatcher) {
    return dispatcher.handleExportable(this);
  }

  public interface Dispatcher<T> {
    T handleArtifact(SemiserialSubvalueExportableIdentityRef s);

    T handleBuiltin(SemiserialSubvalueExportableBuiltin s);
  }

  public interface DefaultDispatcher<T> extends Dispatcher<T> {
    @Override
    default T handleArtifact(SemiserialSubvalueExportableIdentityRef s) {
      throw new RuntimeException("got unexpected semiserial exp ref");
    }

    @Override
    default T handleBuiltin(SemiserialSubvalueExportableBuiltin s) {
      throw new RuntimeException("got unexpected semiserial exp builtin");
    }
  }
}
