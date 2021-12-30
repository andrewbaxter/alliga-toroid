package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class PrototypeArray implements Prototype {
  private final Prototype inner;

  public PrototypeArray(Prototype inner) {
    this.inner = inner;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new DefaultStateSingle<>() {
      TSList<State> elements = new TSList<>();

      @Override
      public Object build(Object context, TSList<Error> errors) {
        TSList<Value> out = new TSList<>();
        boolean bad = false;
        for (State e : elements) {
          Value val = (Value) ((State) e).build(context, errors);
          if (val == null) bad = true;
          out.add(val);
        }
        if (bad) return null;
        return out;
      }

      @Override
      protected DefaultStateArrayBody innerArrayBegin(
          Object context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
        return new DefaultStateArrayBody<>() {
          @Override
          public BaseStateSingle createElementState(
              Object context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
            BaseStateSingle state = inner.create(errors, luxemPath);
            elements.add(state);
            return state;
          }

          @Override
          public Object build(Object context, TSList<Error> errors) {
            return null;
          }
        };
      }
    };
  }
}
