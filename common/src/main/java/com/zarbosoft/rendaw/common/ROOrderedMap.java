package com.zarbosoft.rendaw.common;

import java.util.Iterator;

public interface ROOrderedMap<K, V> extends Iterable<ROPair<K, V>> {
  public static final ROOrderedMap empty = new TSOrderedMap<>();

  V getOpt(K key);

  boolean has(K key);

  Iterator<V> iterValues();

  int size();

  TSOrderedMap<K, V> mut();

  V get(K key);

  ROPair<K, V> getI(int i);
}
