package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class DefaultStateSingle extends BaseStateSingle{
    @Override
    protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPath luxemPath) {
        errors.add(Error.deserializeNotArray(luxemPath));
        return StateErrorArray.state;
    }

    @Override
    protected BaseStateRecord innerEatRecordBegin(TSList<Error> errors, LuxemPath luxemPath) {
        errors.add(Error.deserializeNotRecord(luxemPath));
        return new StateErrorRecord();
    }

    @Override
    protected BaseStateSingle innerEatType(TSList<Error> errors, LuxemPath luxemPath, String name) {
        errors.add(Error.deserializeNotTyped(luxemPath));
        return StateErrorSingle.state;
    }

    @Override
    protected void innerEatPrimitiveUntyped(TSList<Error> errors, LuxemPath luxemPath, String value) {
        errors.add(Error.deserializeNotPrimitive(luxemPath));
    }

    @Override
    public Object build(TSList<Error> errors) {
        return null;
    }
}
