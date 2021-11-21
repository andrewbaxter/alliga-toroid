package com.zarbosoft.rendaw.common;

/**
 * Guarantees iteration is in addition order
 *
 * @param <T>
 */
public interface ROOrderedSetRef<T> extends ROSetRef<T> {
  final ROOrderedSetRef empty = new TSOrderedSet();
}
