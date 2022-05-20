package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
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

public class MortarTupleType extends MortarBaseObjectType implements AutoBuiltinExportable {
  public static final JavaInternalName JVMNAME =
      JavaBytecodeUtils.internalNameFromClass(Tuple.class);
  public static final JavaDataDescriptor DESC = JavaDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarTupleFieldType> fields;

  public MortarTupleType(TSList<MortarTupleFieldType> fields) {
    this.fields = fields;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (int i = 0; i < fields.size(); i++) {
      out.add(Integer.toString(i));
    }
    return out;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeValue(Object inner, long importCacheId, Semiserializer semiserializer, ROList<Exportable> path, ROList<String> accessPath) {
  }

  @Override
  public Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data) {
 // tuple should be identityexportable
 // store type in tuple?


    return data.dispatch(
            new SemiserialSubvalue.DefaultDispatcher<>() {
              @Override
              public Object handleTuple(SemiserialTuple s) {
                TSList<Object> out = new TSList<>();
                for (int i = 0; i < fields.size();i+=1 ) {
                out.add(fields.get(i).graphDesemiserializeValue(context, s.values.get(i)));
                }
                return Tuple.;
              }
            });
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return DESC;
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
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
      if (!field.tupleAssignmentCheckFieldAssignableFrom(
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
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.variableTupleFieldAsValue(context, location, base, field.first);
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) return EvaluateResult.error;
    return field.second.constTupleFieldAsValue(context, location, value, field.first);
  }
}
