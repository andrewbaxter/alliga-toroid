package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.AutoBuiltinClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.half.MortarMethodField;

public class AutoBuiltinMethodFieldType
    implements SimpleValue, MortarHalfType, AutoGraphMixin, LeafValue {
  public final String name;
  public final JVMSharedFuncDescriptor jbcDesc;
  /** Null if null */
  public final MortarHalfDataType returnType;

  public final boolean needsModule;

  public final AutoBuiltinClassType base;

  public AutoBuiltinMethodFieldType(
      AutoBuiltinClassType base,
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
