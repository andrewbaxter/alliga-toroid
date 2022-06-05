package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface SemiserialUnknown extends SemiserialSubvalue {
  public static final Class<? extends SemiserialSubvalue>[] SERIAL_UNION =
      new Class[] {SemiserialExportableRef.class, SemiserialBuiltinRef.class};

  public <T> T dispatchExportable(Dispatcher<T> dispatcher);

  @Override
  default <T> T dispatch(SemiserialSubvalue.Dispatcher<T> dispatcher) {
    return dispatcher.handleUnknown(this);
  }

  public interface Dispatcher<T> {
    T handleExportableRef(SemiserialExportableRef s);

    T handleBuiltinRef(SemiserialBuiltinRef s);
  }

  public interface DefaultDispatcher<T> extends Dispatcher<T> {
    @Override
    default T handleExportableRef(SemiserialExportableRef s) {
      throw new RuntimeException("got unexpected semiserial exportable ref");
    }

    @Override
    default T handleBuiltinRef(SemiserialBuiltinRef s) {
      throw new RuntimeException("got unexpected semiserial builtin ref");
    }
  }
}
