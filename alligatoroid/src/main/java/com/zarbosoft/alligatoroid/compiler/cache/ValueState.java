package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateArrayBegin;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateArrayEnd;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateInt;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateRecordBegin;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateString;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_BUILTIN;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_CACHE;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_INT;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_NULL;
import static com.zarbosoft.alligatoroid.compiler.cache.Cache.CACHE_SUBVALUE_TYPE_STRING;

public class ValueState extends BaseState {
  public final Cache cache;
  public State inner;

  public ValueState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void eatRecordBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    stack.removeLast();
    stack.add(inner = new RecordState(cache));
  }

  @Override
  public void eatType(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
    stack.removeLast();
    switch (name) {
      case CACHE_SUBVALUE_TYPE_NULL:
        {
          inner =
              new BaseState() {
                @Override
                public Object build(TSList<Error> errors) {
                  return null;
                }
              };
          stack.add(StateArrayEnd.state);
          stack.add(StateArrayBegin.state);
          break;
        }
      case CACHE_SUBVALUE_TYPE_STRING:
        {
          stack.add(inner = new StateString());
          break;
        }
      case CACHE_SUBVALUE_TYPE_INT:
        {
          stack.add(inner = new StateInt());
          break;
        }
      case CACHE_SUBVALUE_TYPE_BUILTIN:
        {
          stack.add(inner = new BuiltinState());
          break;
        }
      case CACHE_SUBVALUE_TYPE_CACHE:
        {
          stack.add(inner = new ObjectState(cache));
          break;
        }
      default:
        {
          ok = false;
          errors.add(Error.deserializeCacheSubvalueUnknownType(luxemPath, name));
        }
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    if (inner == null) return Deserializer.errorRet;
    return inner.build(errors);
  }
}
