package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeOther;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.ROMap;

public class MortarHalfAutoObjectType extends MortarHalfObjectType {
  public final JVMSharedJVMName jvmName;
  public final boolean isValue;
  public ROMap<Object, MortarHalfType> fields;

  public MortarHalfAutoObjectType(JVMSharedJVMName jvmName, boolean isValue) {
    this.jvmName = jvmName;
    this.isValue = isValue;
  }

  @Override
  public EvaluateResult valueAccess(
          EvaluationContext context, Location location, MortarValue field0, MortarProtocode lower) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    MortarHalfType field = fields.getOpt(key.concreteValue());
    if (field == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(field.asValue(location, lower));
  }

  @Override
  public MortarValue unlower(Object object) {
    if (isValue) return (MortarValue) object;
    else return new WholeOther(object);
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.fromJVMName(jvmName);
  }
}
