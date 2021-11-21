package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.Value;

public interface WholeValue extends SimpleValue, TreeSerializable {
  public static WholeValue getWhole(Context context, Location location, Value value) {
    if (value == ErrorValue.error) return null;
    if (!(value instanceof WholeValue)) {
      context.module.log.errors.add(new Error.ValueNotWhole(location, value));
      return null;
    }
    return (WholeValue) value;
  }

  Object concreteValue();

  public <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    T handleString(WholeString value);

    T handleBool(WholeBool value);
  }
}
