package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;

public class MortarHalfMethodType implements MortarHalfType, AutoBuiltinExportable, LeafExportable {
  public final String name;
  public final JVMSharedFuncDescriptor jbcDesc;
  /** Null if null */
  public final MortarHalfDataType returnType;

  public final boolean needsModule;

  public final MortarHalfAutoObjectType base;

  public MortarHalfMethodType(
      MortarHalfAutoObjectType base,
      String name,
      JVMSharedFuncDescriptor jbcDesc,
      MortarHalfDataType returnType,
      boolean needsModule) {
    this.base = base;
    this.name = name;
    this.jbcDesc = jbcDesc;
    this.returnType = returnType;
    this.needsModule = needsModule;
  }

  @Override
  public MortarValue asValue(Location location, MortarProtocode lower) {
    return new MortarMethodField(lower, this);
  }
}
