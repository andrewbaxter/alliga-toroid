package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;

public final class MortarMutableType implements MortarDataType {
  public final MortarDataType innerType;

  public MortarMutableType(MortarDataType innerType) {
    this.innerType = innerType;
  }

  @Override
  public int storeOpcode() {
    return innerType.storeOpcode();
  }

  @Override
  public int loadOpcode() {
    return innerType.loadOpcode();
  }

  @Override
  public MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode) {
    return innerType.box(valueCode);
  }

  @Override
  public JVMSharedCodeElement constValueVary(EvaluationContext context, Object value) {
    return innerType.constValueVary(context, value);
  }

  @Override
  public int returnOpcode() {
    return innerType.returnOpcode();
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return innerType.jvmDesc();
  }

  @Override
  public Error checkAssignableFrom(Location location, MortarDataType type) {
    if (type.getClass() == MortarMutableType.class)
      return innerType.checkAssignableFrom(location, ((MortarMutableType) type).innerType);
    else return innerType.checkAssignableFrom(location, type);
  }
}
