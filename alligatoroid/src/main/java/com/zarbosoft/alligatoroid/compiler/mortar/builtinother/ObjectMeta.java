package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class ObjectMeta {
  public final JavaQualifiedName name;
  public final TSList<ObjectMeta> implements_;

  public ObjectMeta(JavaQualifiedName name, TSList<ObjectMeta> implements_) {
    this.name = name;
    this.implements_ = implements_;
  }

  public ObjectMeta() {
    this.name = null;
    this.implements_ = null;
  }

  public boolean canCastTo(ObjectMeta other) {
    return walkParents(t -> t == other);
  }

  private boolean walkParents(Function<ObjectMeta, Boolean> process) {
    TSList<Iterator<ObjectMeta>> stack = new TSList<>();
    stack.add(Arrays.asList(this).iterator());
    while (stack.some()) {
      final Iterator<ObjectMeta> iterator = stack.last();
      ObjectMeta next = iterator.next();
      if (!iterator.hasNext()) stack.removeLast();
      final boolean res = process.apply(next);
      if (res) return true;
      final Iterator<ObjectMeta> parents = next.implements_.iterator();
      if (parents.hasNext()) stack.add(parents);
    }
    return false;
  }
}
