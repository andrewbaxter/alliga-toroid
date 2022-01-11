package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphValue;

public interface BuiltinTypeValue extends OkValue, GraphValue {
  @Override
  default Value type() {
    return Builtin.builtinToBuiltinType.get(getClass());
  }
}
