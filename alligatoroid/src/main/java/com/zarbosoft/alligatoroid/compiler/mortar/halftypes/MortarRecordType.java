package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExtraField;
import com.zarbosoft.alligatoroid.compiler.model.error.MissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

public class MortarRecordType extends MortarBaseObjectType
    implements AutoBuiltinExportable {
  public final TSOrderedMap<Object, ROPair<Integer, MortarTupleFieldType>> fields;

  public MortarRecordType(TSOrderedMap<Object, ROPair<Integer, MortarTupleFieldType>> fields) {
    this.fields = fields;
  }

  public static Object assertConstKey(EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarDataType.assertAssignableFromUnion(
        context,
        location,
        ((DataValue) value).mortarType(),
        MortarStringType.type,
        MortarIntType.type)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return ((ConstDataValue) value).getInner();
  }

  public static ImportId assertConstImportId(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!Meta.autoMortarHalfDataTypes
        .get(ImportId.class)
        .assertAssignableFrom(context.errors, location, value)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (ImportId) ((ConstDataValue) value).getInner();
  }

  public static Integer assertConstIntlike(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof ConstDataValue)) {
      context.errors.add(new ValueNotWhole(location));
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
    context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "constant string or int"));
    return null;
  }

  public static String assertConstString(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarStringType.type.assertAssignableFrom(context.errors, location, value)) return null;
    if (!(value instanceof ConstDataValue)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (String) ((ConstDataValue) value).getInner();
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> field : fields) {
      if (!(field.first instanceof String)) continue;
      out.add((String) field.first);
    }
    return out;
  }

  @Override
  public Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data) {
    return data.dispatch(
        new SemiserialSubvalue.DefaultDispatcher<>() {
          @Override
          public Object handleRecord(SemiserialRecord s) {
            TSMap<Object, Object> out = new TSMap<>();
            for (ROPair<Object, MortarDataType> field : fields) {
              //
            }
            for (ROPair<SemiserialSubvalue, SemiserialSubvalue> datum : s.data) {}
          }
        });
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return MortarTupleType.DESC;
  }

  public ROPair<Integer, MortarTupleFieldType> assertField(EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) return null;
    final ROPair<Integer, MortarTupleFieldType> field = fields.getOpt(fieldKey);
    if (field == null) {
      context.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(field.first, field.second);
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.constTupleFieldAsValue(context,location,value, field.first);
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
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> e : ((MortarRecordType) type).fields) {
      otherKeys.add(e.first);
    }
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> field : fields) {
      final ROPair<Integer, MortarTupleFieldType> otherField = ((MortarRecordType) type).fields.getOpt(field.first);
      if (otherField == null) {
        errors.add(new MissingField(location, path, field.first));
        bad = true;
        continue;
      }
      otherKeys.remove(field.first);
      if (!field.second.second.tupleAssignmentCheckFieldAssignableFrom(
          errors, location, otherField.second, path.mut().add(field.first))) bad = true;
    }
    for (Object otherKey : otherKeys) {
      bad = true;
      errors.add(new ExtraField(location, path, otherKey));
    }
    return bad;
  }

  @Override
  public EvaluateResult variableValueAccess(EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    final ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.variableTupleFieldAsValue(context,location,base, field.first);
  }
}
