package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateArray;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateInt;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateString;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_BUILTIN;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_CACHE;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_INT;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_NULL;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_STRING;

public class ValueState extends DefaultStateSingle {
  public final Cache cache;
  public BaseStateSingle inner;

  public ValueState(Cache cache) {
    this.cache = cache;
  }

  @Override
  protected BaseStateSingle innerEatType(TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    switch (name) {
      case CACHE_SUBVALUE_TYPE_NULL:
        return inner =
            new DefaultStateSingle() {
              @Override
              protected DefaultStateArray innerArrayBegin(
                  TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new DefaultStateArray() {
                  @Override
                  public BaseStateSingle createElementState(
                      TSList<Error> errors, LuxemPathBuilder luxemPath) {
                    return StateErrorSingle.state;
                  }

                  @Override
                  public Object build(TSList<Error> errors) {
                    return null;
                  }
                };
              }
            };
      case CACHE_SUBVALUE_TYPE_STRING:
        return inner = new StateString();
      case CACHE_SUBVALUE_TYPE_INT:
        return inner = new StateInt();
      case CACHE_SUBVALUE_TYPE_BUILTIN:
        return inner = new BuiltinState();
      case CACHE_SUBVALUE_TYPE_CACHE:
        return inner = new ObjectState(cache);
      default:
        {
          errors.add(new Error.DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    if (inner == null) return Deserializer.errorRet;
    return inner.build(errors);
  }
}
