package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;

public class PrototypeROSetRef implements ProtoType {
  private final ProtoType inner;

  public PrototypeROSetRef(ProtoType inner) {
    this.inner = inner;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new DefaultStateSingle<>() {
      TSList<State> elements = new TSList<>();

      @Override
      public Object build(Object context, TSList<Error> errors) {
        TSSet out = new TSSet<>();
        boolean bad = false;
        for (State e : elements) {
          Object obj = ((State) e).build(context, errors);
          if (obj == null) {
              bad = true;
          }
          out.add(obj);
        }
        if (bad) {
            return null;
        }
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
