package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class BuiltinState extends BaseState {
  private String key;

  @Override
  public void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    key = value;
    stack.removeLast();
  }

  @Override
  public Object build(TSList<Error> errors) {
    return Cache.builtinMap.get(key);
  }
}
