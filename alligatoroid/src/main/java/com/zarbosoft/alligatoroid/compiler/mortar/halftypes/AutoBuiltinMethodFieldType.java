package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodField;

public class AutoBuiltinMethodFieldType
    implements SimpleValue, MortarHalfType, AutoGraphMixin, LeafValue {
  public final String name;
  public final JVMSharedFuncDescriptor jbcDesc;
  /** Null if null */
  public final MortarHalfDataType returnType;

  public final boolean needsModule;

  public final MortarHalfAutoType base;

  public AutoBuiltinMethodFieldType(
      MortarHalfAutoType base,
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
  public Value asValue(Location location, MortarProtocode lower) {
    return new MortarMethodField(lower, this);
  }
}
