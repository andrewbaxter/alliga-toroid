package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class MortarObjectInnerType {
  public final JavaQualifiedName name;
  public final TSList<MortarObjectInnerType> implements_;

  public MortarObjectInnerType(JavaQualifiedName name, TSList<MortarObjectInnerType> implements_) {
    this.name = name;
    this.implements_ = implements_;
  }

  public boolean canAssignTo(MortarObjectInnerType other) {
    return walkParents(t -> t == other);
  }

  private boolean walkParents(Function<MortarObjectInnerType, Boolean> process) {
    TSList<Iterator<MortarObjectInnerType>> stack = new TSList<>();
    stack.add(Arrays.asList(this).iterator());
    while (stack.some()) {
      final Iterator<MortarObjectInnerType> iterator = stack.last();
      MortarObjectInnerType next = iterator.next();
      if (!iterator.hasNext()) {
        stack.removeLast();
      }
      final boolean res = process.apply(next);
      if (res) {
        return true;
      }
      final Iterator<MortarObjectInnerType> parents = next.implements_.iterator();
      if (parents.hasNext()) {
        stack.add(parents);
      }
    }
    return false;
  }
}
