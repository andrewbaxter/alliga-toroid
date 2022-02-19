package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExtraField;
import com.zarbosoft.alligatoroid.compiler.model.error.MissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstIntlike;

public class MortarTupleType extends MortarObjectType implements AutoBuiltinExportable {
  public static final JVMSharedJVMName JVMNAME = JVMSharedJVMName.fromClass(Tuple.class);
  public static final JVMSharedDataDescriptor DESC = JVMSharedDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarDataType> fields;

  public MortarTupleType(TSList<MortarDataType> fields) {
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
  public JVMSharedDataDescriptor jvmDesc() {
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
    final TSList<MortarDataType> otherFields = ((MortarTupleType) type).fields;
    boolean bad = false;
    for (int i = 0; i < fields.size(); i++) {
      final MortarDataType field = fields.get(i);
      if (i >= otherFields.size()) {
        errors.add(new MissingField(location, path, i));
        bad = true;
        continue;
      }
      if (!field.checkAssignableFrom(errors, location, otherFields.get(i), path.mut().add(i)))
        bad = true;
    }
    for (int i = fields.size(); i < otherFields.size(); i += 1) {
      bad = true;
      errors.add(new ExtraField(location, path, i));
    }
    return bad;
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry targetCarry, Value field0) {
    Integer key = assertConstIntlike(context, location, field0);
    if (key == null) return EvaluateResult.error;
    if (key < 0 || key >= fields.size()) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    MortarDataType field = fields.get(key);
    JVMSharedCode out = new JVMSharedCode();
    out.add(targetCarry.half(context));
    out.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location),
            JVMNAME,
            "get",
            JVMSharedFuncDescriptor.fromParts(
                JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.INT)));
    out.add(JVMSharedCode.cast(field.jvmDesc()));
    return EvaluateResult.pure(field.deferredStackAsValue(out));
  }
}
