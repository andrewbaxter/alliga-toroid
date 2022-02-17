package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;

public final class MortarImmutableType implements MortarDataType, AutoBuiltinExportable {
  public static final MortarImmutableType nullType = new MortarImmutableType(MortarNullType.type);
  public static final MortarImmutableType intType = new MortarImmutableType(MortarIntType.type);
  public static final MortarImmutableType boolType = new MortarImmutableType(MortarBoolType.type);
  public static final MortarImmutableType stringType =
      new MortarImmutableType(MortarStringType.type);

  public final MortarDataType innerType;

  public MortarImmutableType(MortarDataType innerType) {
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
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    errors.add(new SetNotSupported(location));
    return false;
  }
}
