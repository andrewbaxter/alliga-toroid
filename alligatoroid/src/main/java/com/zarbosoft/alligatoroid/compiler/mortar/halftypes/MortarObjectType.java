package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstKey;

public class MortarObjectType extends MortarBaseObjectType implements BuiltinAutoExportable {
  public ObjectMeta meta;
  public ROMap<Object, Field> fields;

  public static MortarObjectType create(ObjectMeta meta, ROMap<Object, Field> fields) {
    final MortarObjectType out = new MortarObjectType();
    out.meta = meta;
    out.fields = fields;
    out.postInit();
    return out;
  }

  @Override
  public MortarDataType type_fork() {
    TSMap<Object, Field> forkedFields = new TSMap<>();
    for (Map.Entry<Object, Field> fieldType : fields) {
      forkedFields.put(fieldType.getKey(), fieldType.getValue().objectFieldFork());
    }
    return MortarObjectType.create(meta, forkedFields);
  }

  @Override
  public ROList<String> type_traceFields(
      EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<Object, Field> field : fields) {
      if (!(field.getKey() instanceof String)) continue;
      out.add((String) field.getKey());
    }
    return out;
  }

  @Override
  public JavaBytecode type_castTo(MortarDataPrototype prototype, MortarDeferredCode code) {
    return code.consume();
  }

  @Override
  public boolean type_canCastTo(
      MortarDataPrototype prototype) {
    if (!(prototype instanceof MortarObjectPrototype)) {
      return false;
    }
    return meta.canCastTo(((MortarObjectPrototype) prototype).meta);
  }

  @Override
  public String toString() {
    return meta.name.toString();
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
  public EvaluateResult type_variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    final ROPair<Object, Field> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.variableObjectFieldAsValue(context, location, base);
  }

  @Override
  public EvaluateResult type_constValueAccess(
      EvaluationContext context, Location location, Object base, Value field0) {
    final ROPair<Object, Field> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.constObjectFieldAsValue(context, location, base);
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return JavaDataDescriptor.fromJVMName(meta.name.asInternalName());
  }
}
