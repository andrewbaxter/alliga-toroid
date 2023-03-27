package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.MortarInvalidCast;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataConstValue;

public interface MortarDataPrototype {
  JavaDataDescriptor prototype_jvmDesc();

  Value prototype_stackAsValue(JavaBytecode code);

  JavaBytecode prototype_returnBytecode();

  MortarDataType prototype_newType();

  MortarProtofield prototype_newProtofield(ObjectMeta baseMeta, String name);

  default Value prototype_constAsValue(Object data) {
    return MortarDataConstValue.create(prototype_newType(), data);
  }

  public default JavaBytecode prototype_cast(
      EvaluationContext evaluationContext, Location location, Value value) {
    if (value instanceof MortarCastable
        && ((MortarCastable) value).canCastTo(this)) {
      return ((MortarCastable) value).castTo(this);
    } else {
      evaluationContext.errors.add(new MortarInvalidCast(location, this));
      return null;
    }
  }
}
