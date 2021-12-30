package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.rendaw.common.ROList;

public class Tuple extends MortarHalfObjectType {
  public final ROList<Object> data;

  public Tuple(ROList<Object> data) {
    this.data = data;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.objDescriptorFromReal(getClass());
  }

  @Override
  public Value unlower(Object object) {
    return (Tuple) object;
  }
}
