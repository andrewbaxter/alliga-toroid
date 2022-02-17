package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class MortarAutoObjectType extends MortarObjectType implements SingletonBuiltinExportable {
  public final JVMSharedJVMName jvmName;
  public final boolean isValue;
  private final Class klass;
  public ROMap<Object, MortarFieldType> fields;
  public ROList<MortarDataType> inherits;

  public MortarAutoObjectType(Class klass, boolean isValue) {
    this.klass = klass;
    this.jvmName = JVMSharedJVMName.fromClass(klass);
    this.isValue = isValue;
  }

  private boolean walkParents(Function<MortarDataType, Boolean> process) {
    TSList<Iterator<MortarDataType>> stack = new TSList<>();
    stack.add(Arrays.asList((MortarDataType) this).iterator());
    while (stack.some()) {
      final Iterator<MortarDataType> iterator = stack.last();
      MortarDataType next = iterator.next();
      if (!iterator.hasNext()) stack.removeLast();
      final boolean res = process.apply(next);
      if (res) return true;
      if (next instanceof MortarAutoObjectType) {
        final Iterator<MortarDataType> parents = ((MortarAutoObjectType) next).inherits.iterator();
        if (parents.hasNext()) stack.add(parents);
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return klass.getSimpleName();
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (!(type instanceof MortarAutoObjectType)
        || !((MortarAutoObjectType) type).walkParents(t -> t == this)) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
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
