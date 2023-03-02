package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
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

  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (!(type instanceof MortarObjectType) || !walkParents(t -> t == this)) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  boolean assertAssignableFrom(EvaluationContext context, Location location, MortarDataType type) {
    return checkAssignableFrom(context.moduleContext.getErrors(), location, type, new TSList<>());
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

  public boolean assertAssignableFrom(TSList<Error> errors, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
    }
    return checkAssignableFrom(errors, location, ((DataValue) value).mortarType(), new TSList<>());
  }
}
