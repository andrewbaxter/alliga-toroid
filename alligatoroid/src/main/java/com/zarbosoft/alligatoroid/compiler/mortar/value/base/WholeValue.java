package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;

public interface WholeValue extends SimpleValue, TreeSerializable {
  public static WholeValue getWhole(EvaluationContext context, Location location, Value value) {
    if (value == ErrorValue.error) return null;
    if (!(value instanceof WholeValue)) {
      context.moduleContext.errors.add(new ValueNotWhole(location, value));
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

  public class DefaultDispatcher<T> implements Dispatcher<T> {
    public final T defaultRet;

    public DefaultDispatcher(T defaultRet) {
      this.defaultRet = defaultRet;
    }

    @Override
    public T handleString(WholeString value) {
      return defaultRet;
    }

    @Override
    public T handleBool(WholeBool value) {
      return defaultRet;
    }
  }
}