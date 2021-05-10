package com.zarbosoft.rendaw.common;

import java.util.Set;
import java.util.function.Consumer;

public interface ROSetRef<T> extends Iterable<T> {
  Set<T> inner_();

  default TSSet<T> union(ROSetRef<T> other) {
    return mut().addAll(other);
  }

  default void forEach(Consumer<? super T> action) {
    throw new Assertion();
  }

  default TSSet<T> intersect(ROSetRef<T> other) {
    TSSet<T> copy = mut();
    copy.inner_().retainAll(other.inner_());
    return copy;
  }

  default TSSet<T> difference(ROSetRef<T> other) {
    return mut().removeAll(other);
  }

  default boolean isEmpty() {
    return size() == 0;
  }

  default boolean some() {
    return size() > 0;
  }

  default boolean none() {
    return size() == 0;
  }

  boolean containsAll(ROSetRef<T> other);

  default boolean containsAny(ROSetRef<T> other) {
    for (T t : other) {
      if (contains(t)) return true;
    }
    return false;
  }

  int size();

  ROSet<T> own();

  TSSet<T> mut();

  boolean contains(T t);
}
