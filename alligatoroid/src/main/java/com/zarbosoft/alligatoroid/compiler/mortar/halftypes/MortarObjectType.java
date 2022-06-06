package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class MortarObjectType extends MortarBaseObjectType implements BuiltinAutoExportable {
  public JavaQualifiedName name;
  public ROMap<Object, Field> fields;
  public ROList<MortarDataType> implements_;

  public static MortarObjectType create(
      JavaQualifiedName name, ROMap<Object, Field> fields, ROList<MortarDataType> implements_) {
    final MortarObjectType out = new MortarObjectType();
    out.name = name;
    out.fields = fields;
    out.implements_ = implements_;
    out.postInit();
    return out;
  }

  @Override
  public MortarDataType fork() {
    TSMap<Object, Field> forkedFields = new TSMap<>();
    for (Map.Entry<Object, Field> fieldType : fields) {
      forkedFields.put(fieldType.getKey(), fieldType.getValue().objectFieldFork());
    }
    return MortarObjectType.create(name, forkedFields, implements_);
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<Object, Field> field : fields) {
      if (!(field.getKey() instanceof String)) continue;
      out.add((String) field.getKey());
    }
    return out;
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
      if (next instanceof MortarObjectType) {
        final Iterator<MortarDataType> parents = ((MortarAutoObjectType) next).inherits.iterator();
        if (parents.hasNext()) stack.add(parents);
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return name.toString();
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (!(type instanceof MortarObjectType)
        || !((MortarObjectType) type).walkParents(t -> t == this)) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  public ROPair<Object, Field> assertField(
      EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) return null;
    final Field field = fields.getOpt(fieldKey);
    if (field == null) {
      context.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(fieldKey, field);
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    final ROPair<Object, Field> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.variableObjectFieldAsValue(context, location, base, name.asInternalName());
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object base, Value field0) {
    final ROPair<Object, Field> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.constObjectFieldAsValue(context, location, base);
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.fromJVMName(name.asInternalName());
  }
}
