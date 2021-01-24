package com.zarbosoft.rendaw.common;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Maintains a mapping where entries may be garbage collected. Each value must have a reference to
 * an object that acts as the key -
 *
 * @param <K>
 * @param <V>
 */
public class WeakCache<K, V> {
  private final WeakHashMap<K, WeakReference<V>> map = new WeakHashMap<>();
  private final Function<V, K> getKey;

  public WeakCache(final Function<V, K> getKey) {
    this.getKey = getKey;
  }

  public void add(final V v) {
    map.put(getKey.apply(v), new WeakReference<>(v));
  }

  public V get(final K k) {
    final WeakReference<V> temp = map.get(k);
    if (temp == null) return null;
    return temp.get();
  }

  public void clear() {
    map.clear();
  }

  public V getOrCreate(final K k, final Function<K, V> supplier) {
    V v = get(k);
    if (v == null) {
      v = supplier.apply(k);
      add(v);
    }
    return v;
  }
}
