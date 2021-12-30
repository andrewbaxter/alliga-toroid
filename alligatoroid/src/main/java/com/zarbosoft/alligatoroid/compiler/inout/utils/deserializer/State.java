package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public interface State<C, T> {
  default void popSelf(TSList<State> stack) {
    if (stack.removeLast() != this) throw new Assertion();
  }

  void eatArrayBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath);

  void eatArrayEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath);

  void eatRecordBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath);

  void eatRecordEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath);

  void eatType(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String name);

  void eatPrimitive(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String value);

  public T build(C context, TSList<Error> errors);
}
