package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public interface ProtoType {
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath);
}
