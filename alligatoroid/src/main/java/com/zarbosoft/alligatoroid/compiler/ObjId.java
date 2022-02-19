package com.zarbosoft.alligatoroid.compiler;

/** Wrapper for creating identity-based hash maps with exportables. */
public class ObjId<T> {
  public final T objId;

  public ObjId(T objId) {
    this.objId = objId;
  }

  @Override
  public boolean equals(Object o) {
    return o == objId;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(objId);
  }
}
