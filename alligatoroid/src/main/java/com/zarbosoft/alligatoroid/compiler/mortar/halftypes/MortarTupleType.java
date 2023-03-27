package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExtraField;
import com.zarbosoft.alligatoroid.compiler.model.error.MissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstIntlike;

public class MortarTupleType extends MortarBaseObjectType implements BuiltinAutoExportable {
  public static final JavaInternalName JVMNAME =
      JavaBytecodeUtils.internalNameFromClass(Tuple.class);
  public static final JavaDataDescriptor DESC = JavaDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarTupleFieldType> fields;

  public MortarTupleType(TSList<MortarTupleFieldType> fields) {
    this.fields = fields;
  }

  @Override
  public ROList<String> type_traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (int i = 0; i < fields.size(); i++) {
      out.add(Integer.toString(i));
    }
    return out;
  }

  @Override
  public JavaBytecode type_castTo(MortarDataPrototype prototype, MortarDeferredCode code) {
    TODO();
  }

  @Override
  public boolean type_canCastTo(MortarDataPrototype prototype) {
    TODO();
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return DESC;
  }

  @Override
  public boolean type_checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof ImmutableType) type = ((ImmutableType) type).innerType;
    if (!(type instanceof MortarTupleType)) {
      errors.add(new WrongType(location, path, type.toString(), "tuple"));
      return false;
    }
    final TSList<MortarTupleFieldType> otherFields = ((MortarTupleType) type).fields;
    boolean bad = false;
    for (int i = 0; i < fields.size(); i++) {
      final MortarTupleFieldType field = fields.get(i);
      if (i >= otherFields.size()) {
        errors.add(new MissingField(location, path, i));
        bad = true;
        continue;
      }
      if (!field.tuple_fieldtype_assignmentCheckFieldAssignableFrom(
          errors, location, otherFields.get(i), path.mut().add(i))) bad = true;
    }
    for (int i = fields.size(); i < otherFields.size(); i += 1) {
      bad = true;
      errors.add(new ExtraField(location, path, i));
    }
    return bad;
  }

  public ROPair<Integer, MortarTupleFieldType> assertField(
      EvaluationContext context, Location location, Value field) {
    Integer key = assertConstIntlike(context, location, field);
    if (key == null) return null;
    if (key < 0 || key >= fields.size()) {
      context.errors.add(new NoField(location, key));
      return null;
    }
    return new ROPair<>(key, fields.get(key));
  }

  @Override
  public EvaluateResult type_variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.tuple_fieldtype_variableAsValue(context, location, base, field.first);
  }

  @Override
  public EvaluateResult type_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.tuple_fieldtype_constAsValue(context, location, value, field.first);
  }
}
