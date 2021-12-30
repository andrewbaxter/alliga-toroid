package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphValue;

public interface BuiltinValue extends OkValue, GraphValue {
  @Override
  default Value type() {
    return Builtin.builtinToBuiltinType.get(getClass());
  }
}
