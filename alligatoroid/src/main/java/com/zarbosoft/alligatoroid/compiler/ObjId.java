package com.zarbosoft.alligatoroid.compiler;

/** Wrapper for creating identity-based hash maps with exportables. */
public class ObjId<T> {
  public final T obj;

  public ObjId(T obj) {
    this.obj = obj;
  }

  @Override
  public boolean equals(Object o) {
    return ((ObjId)o).obj == obj;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(obj);
  }
}
