package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateArray;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateErrorSingle;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class ObjectRootState extends DefaultStateSingle {
  private final Cache cache;
  private State inner;

  public ObjectRootState(Cache cache) {
    this.cache = cache;
  }

  @Override
  protected BaseStateSingle innerEatType(TSList<Error> errors, LuxemPath luxemPath, String name) {
    switch (name) {
      case Cache.CACHE_OBJECT_TYPE_OUTPUT:
        return new DefaultStateSingle() {
          @Override
          protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPath luxemPath) {
            OutputTypeState out = new OutputTypeState(cache);
            inner = out;
            return out;
          }
        };
      case Cache.CACHE_OBJECT_TYPE_BUILTIN:
        return new DefaultStateSingle() {
          @Override
          protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPath luxemPath) {
            BuiltinTypeState out = new BuiltinTypeState(cache);
            inner = out;
            return out;
          }
        };
      default:
        {
          errors.add(Error.deserializeCacheObjectUnknownType(luxemPath, name));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    return inner.build(errors);
  }
}
