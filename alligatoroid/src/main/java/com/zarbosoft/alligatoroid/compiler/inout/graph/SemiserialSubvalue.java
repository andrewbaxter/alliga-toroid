package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;

public interface SemiserialSubvalue extends TreeSerializable {
  public <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    T handleInt(SemiserialInt s);

    T handleString(SemiserialString s);

    T handleRef(SemiserialRef s);

    T handleRecord(SemiserialRecord s);

    T handleTuple(SemiserialTuple s);

    T handleBool(SemiserialBool s);

    T handleType(SemiserialType s);
  }

  public static class DefaultDispatcher<T> implements Dispatcher<T> {
    @Override
    public T handleInt(SemiserialInt s) {
      throw new RuntimeException("got unexpected semiserial int");
    }

    @Override
    public T handleBool(SemiserialBool s) {
      throw new RuntimeException("got unexpected semiserial bool");
    }

    @Override
    public T handleType(SemiserialType s) {
      throw new RuntimeException("got unexpected semiserial type");
    }

    @Override
    public T handleString(SemiserialString s) {
      throw new RuntimeException("got unexpected semiserial string");
    }

    @Override
    public T handleRef(SemiserialRef s) {
      throw new RuntimeException("got unexpected semiserial ref");
    }

    @Override
    public T handleRecord(SemiserialRecord s) {
      throw new RuntimeException("got unexpected semiserial record");
    }

    @Override
    public T handleTuple(SemiserialTuple s) {
      throw new RuntimeException("got unexpected semiserial tuple");
    }
  }
}
