package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExtraField;
import com.zarbosoft.alligatoroid.compiler.model.error.MissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

public class MortarRecordType extends MortarObjectType
    implements AutoBuiltinExportable, Exportable {
  public static final JVMSharedJVMName JVMNAME = JVMSharedJVMName.fromClass(Record.class);
  public static final JVMSharedDataDescriptor DESC = JVMSharedDataDescriptor.fromJVMName(JVMNAME);
  public final TSOrderedMap<Object, MortarDataType> fields;

  public MortarRecordType(TSOrderedMap<Object, MortarDataType> fields) {
    this.fields = fields;
  }

  public static Object assertConstKey(EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.moduleContext.errors.add(
          new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarDataType.assertAssignableFromUnion(
        context,
        location,
        ((DataValue) value).mortarType(),
        MortarStringType.type,
        MortarIntType.type)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return null;
    }
    return ((ConstDataValue) value).getInner();
  }

  public static ModuleId assertConstModuleId(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.moduleContext.errors.add(
          new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!Meta.autoMortarHalfDataTypes
        .get(ModuleId.class)
        .assertAssignableFrom(context, location, value)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (ModuleId) ((ConstDataValue) value).getInner();
  }

  public static Integer assertConstIntlike(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof ConstDataValue)) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return null;
    }
    if (MortarIntType.type.checkAssignableFrom(location, value)) {
      return (Integer) ((ConstDataValue) value).getInner();
    } else if (MortarStringType.type.checkAssignableFrom(location, value)) {
      try {
        return Integer.parseInt((String) ((ConstDataValue) value).getInner());
      } catch (Exception ignored) {
      }
    }
    context.moduleContext.errors.add(
        new WrongType(location, new TSList<>(), value.toString(), "constant string or int"));
    return null;
  }

  public static String assertConstString(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.moduleContext.errors.add(
          new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarStringType.type.assertAssignableFrom(context, location, value)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (String) ((ConstDataValue) value).getInner();
  }

  @Override
  public ROList<String> traceFields(Object inner) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, MortarDataType> field : fields) {
      if (!(field.first instanceof String)) continue;
      out.add((String) field.first);
    }
    return out;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }

  public FoundField assertField(EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) return null;
    final MortarDataType field = fields.getOpt(fieldKey);
    if (field == null) {
      context.moduleContext.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new FoundField(fieldKey, field, (VariableDataValue) field0);
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final FoundField field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return EvaluateResult.pure(field.type.constAsValue(((Record) value).get(field.key)));
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (!(type instanceof MortarRecordType)) {
      errors.add(new WrongType(location, path, type.toString(), "record"));
      return false;
    }
    boolean bad = false;
    final TSSet otherKeys = new TSSet<>();
    for (ROPair<Object, MortarDataType> e : ((MortarRecordType) type).fields) {
      otherKeys.add(e.first);
    }
    for (ROPair<Object, MortarDataType> field : fields) {
      final MortarDataType otherField = ((MortarRecordType) type).fields.getOpt(field.first);
      if (otherField == null) {
        errors.add(new MissingField(location, path, field.first));
        bad = true;
        continue;
      }
      otherKeys.remove(field.first);
      if (!field.second.checkAssignableFrom(
          errors, location, otherField, path.mut().add(field.first))) bad = true;
    }
    for (Object otherKey : otherKeys) {
      bad = true;
      errors.add(new ExtraField(location, path, otherKey));
    }
    return bad;
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry targetCarry, Value field0) {
    final FoundField field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    JVMSharedCode out = new JVMSharedCode();
    out.add(targetCarry.half(context));
    out.add(field.fieldValue.mortarVaryCode(context, location).half(context));
    out.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location),
            JVMNAME,
            "get",
            JVMSharedFuncDescriptor.fromParts(
                JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.OBJECT)));
    out.add(JVMSharedCode.cast(field.type.jvmDesc()));
    return EvaluateResult.pure(field.type.deferredStackAsValue(out));
  }

  public static class FoundField {
    private final Object key;
    private final MortarDataType type;
    private final VariableDataValue fieldValue;

    public FoundField(Object key, MortarDataType type, VariableDataValue fieldValue) {
      this.key = key;
      this.type = type;
      this.fieldValue = fieldValue;
    }
  }
}
