package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class PrototypeInt implements ProtoType {
  public static final PrototypeInt instance = new PrototypeInt();

  private PrototypeInt() {}

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateInt();
  }
}
