package com.zarbosoft.rendaw.common;

import java.util.Iterator;
import java.util.List;

public class ReverseIterable<T> implements Iterable<T> {
  private final List<T> data;

  public ReverseIterable(ROList<T> data) {
    this.data = data.inner_();
  }
  public ReverseIterable(List<T> data) {
    this.data = data;
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return i < data.size();
      }

      @Override
      public T next() {
        return data.get(data.size() - 1 - i++);
      }
    };
  }
}
