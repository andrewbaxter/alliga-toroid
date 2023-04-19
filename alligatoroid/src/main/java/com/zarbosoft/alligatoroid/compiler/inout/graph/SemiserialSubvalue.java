package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface SemiserialSubvalue {
  public static final Class<? extends SemiserialSubvalue>[] SERIAL_UNION =
      new Class[] {
        SemiserialInt.class,
        SemiserialString.class,
        SemiserialExportableRef.class,
        SemiserialBuiltinRef.class,
        SemiserialExportable.class,
        SemiserialRecord.class,
        SemiserialTuple.class,
        SemiserialBool.class,
      };

  public <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    T handleInt(SemiserialInt s);

    T handleString(SemiserialString s);

    T handleRecord(SemiserialRecord s);

    T handleTuple(SemiserialTuple s);

    T handleBool(SemiserialBool s);

    T handleUnknown(SemiserialRef s);
  }

  public interface DefaultDispatcher<T> extends Dispatcher<T> {
    default T handleDefault(SemiserialSubvalue s) {
      throw new RuntimeException("got unexpected semiserial type");
    }

    @Override
    default T handleInt(SemiserialInt s) {
      return handleDefault(s);
    }

    @Override
    default T handleBool(SemiserialBool s) {
      return handleDefault(s);
    }

    @Override
    default T handleUnknown(SemiserialRef s) {
      return handleDefault(s);
    }

    @Override
    default T handleString(SemiserialString s) {
      return handleDefault(s);
    }

    @Override
    default T handleRecord(SemiserialRecord s) {
      return handleDefault(s);
    }

    @Override
    default T handleTuple(SemiserialTuple s) {
      return handleDefault(s);
    }
  }
}
