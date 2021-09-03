package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateArrayBegin;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class ObjectRootState extends BaseState {
    private final Cache cache;
    private State inner;

    public ObjectRootState(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void eatType(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
        stack.removeLast();
        switch (name) {
            case Cache.CACHE_OBJECT_TYPE_OUTPUT: {
                stack.add(inner=new OutputTypeState(cache));
                stack.add(StateArrayBegin.state);
                break;
            }
            case Cache.CACHE_OBJECT_TYPE_BUILTIN:{
                stack.add(inner=new BuiltinTypeState(cache));
                stack.add(StateArrayBegin.state);
                break;
            }
            default:{
                errors.add(Error.deserializeCacheObjectUnknownType(luxemPath, name));
                ok = false;
            }
        }
    }

    @Override
    public Object build(TSList<Error> errors) {
        return inner.build(errors);
    }
}
