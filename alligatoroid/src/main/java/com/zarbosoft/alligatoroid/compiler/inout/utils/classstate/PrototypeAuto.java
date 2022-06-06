package com.zarbosoft.alligatoroid.compiler.inout.utils.classstate;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.ProtoType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class PrototypeAuto implements ProtoType {
  private final ClassInfo info;

  public PrototypeAuto(Class klass) {
    info = new ClassInfo(null);
    info.fill(klass);
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateRecord(new StateClassBody(luxemPath, info));
  }
}
