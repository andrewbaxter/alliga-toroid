package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StatePrototypeArray implements StatePrototype {
  private final StatePrototype inner;

  public StatePrototypeArray(StatePrototype inner) {
    this.inner = inner;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPath luxemPath) {
    return new DefaultStateSingle() {
      TSList<State> elements = new TSList<>();

      @Override
      public Object build(TSList<Error> errors) {
        TSList<Value> out = new TSList<>();
        boolean bad = false;
        for (State e : elements) {
          Value val = (Value) ((State) e).build(errors);
          if (val == null) bad = true;
          out.add(val);
        }
        if (bad) return null;
        return out;
      }

      @Override
      protected DefaultStateArray innerArrayBegin(TSList<Error> errors, LuxemPath luxemPath) {
        return new DefaultStateArray() {
          @Override
          public BaseStateSingle createElementState(TSList<Error> errors, LuxemPath luxemPath) {
            BaseStateSingle state = inner.create(errors, luxemPath);
            elements.add(state);
            return state;
          }

          @Override
          public Object build(TSList<Error> errors) {
            return null;
          }
        };
      }
    };
  }
}
