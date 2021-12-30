package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotArray;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotPrimitive;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotRecord;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotTyped;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public abstract class DefaultStateSingle<C, T> extends BaseStateSingle<C, T> {
  @Override
  protected BaseStateArrayBody innerArrayBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    errors.add(new DeserializeNotArray(luxemPath.render()));
    return StateErrorArrayBody.state;
  }

  @Override
  protected BaseStateRecordBody innerEatRecordBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    errors.add(new DeserializeNotRecord(luxemPath.render()));
    return new StateErrorRecordBody();
  }

  @Override
  protected BaseStateSingle innerEatType(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    errors.add(new DeserializeNotTyped(luxemPath.render()));
    return StateErrorSingle.state;
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    errors.add(new DeserializeNotPrimitive(luxemPath.render()));
  }
}
