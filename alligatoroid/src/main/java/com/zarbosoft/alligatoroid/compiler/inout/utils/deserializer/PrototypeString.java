package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class PrototypeString implements ProtoType {
  public static final PrototypeString instance = new PrototypeString();

  private PrototypeString() {}

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }
}
