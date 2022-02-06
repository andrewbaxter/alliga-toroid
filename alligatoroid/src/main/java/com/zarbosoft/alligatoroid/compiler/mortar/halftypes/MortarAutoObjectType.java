package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class MortarAutoObjectType extends MortarObjectType {
  public final JVMSharedJVMName jvmName;
  public final boolean isValue;
  public ROMap<Object, MortarFieldType> fields;

  public MortarAutoObjectType(JVMSharedJVMName jvmName, boolean isValue) {
    this.jvmName = jvmName;
    this.isValue = isValue;
  }

  public ROPair<Object, MortarFieldType> assertField(
      EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) return null;
    final MortarFieldType field = fields.getOpt(fieldKey);
    if (field == null) {
      context.moduleContext.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(fieldKey, field);
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry targetCarry, Value field0) {
    final ROPair<Object, MortarFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.variableFieldAsValue(context, location, targetCarry, this);
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final ROPair<Object, MortarFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.constFieldAsValue(context, location, value);
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.fromJVMName(jvmName);
  }
}
