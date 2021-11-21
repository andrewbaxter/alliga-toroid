package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class DefaultStateSingle extends BaseStateSingle {
  @Override
  protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    errors.add(new Error.DeserializeNotArray(luxemPath.render()));
    return StateErrorArray.state;
  }

  @Override
  protected BaseStateRecord innerEatRecordBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    errors.add(new Error.DeserializeNotRecord(luxemPath.render()));
    return new StateErrorRecord();
  }

  @Override
  protected BaseStateSingle innerEatType(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    errors.add(new Error.DeserializeNotTyped(luxemPath.render()));
    return StateErrorSingle.state;
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    errors.add(new Error.DeserializeNotPrimitive(luxemPath.render()));
  }

  @Override
  public Object build(TSList<Error> errors) {
    return null;
  }
}
