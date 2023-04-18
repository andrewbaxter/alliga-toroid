package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstructionInt;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
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
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeDup;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.Map;

import static org.objectweb.asm.Opcodes.ANEWARRAY;

public class MortarTupleTypestate implements BuiltinAutoExportable, MortarDataTypestate {
  public final ROList<ROPair<Object, MortarTupleFieldstate>> fields;
  public final ROMap<Object, Integer> fieldLookup;

  public MortarTupleTypestate(ROList<ROPair<Object, MortarTupleFieldstate>> fields) {
    this.fields = fields;
    this.fieldLookup =
        TSMap.createWith(
            m -> {
              for (int i = 0; i < fields.size(); i++) {
                m.put(fields.get(i).first, i);
              }
            });
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  public static Object assertConstKey(EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarDataTypestate.type_assertAssignableFromUnion(
        context,
        location,
        ((MortarDataValue) value).type(),
        MortarStringTypestate.typestate,
        MortarIntTypestate.typestate)) {
      return null;
    }
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return ((MortarDataValueConst) value).getInner();
  }

  public static ImportId assertConstImportId(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!StaticAutogen.autoMortarHalfObjectTypes
        .get(ImportId.class)
        .assertAssignableFrom(context.errors, location, value)) {
      return null;
    }
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (ImportId) ((MortarDataValueConst) value).getInner();
  }

  public static Integer assertConstIntlike(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    if (MortarIntTypestate.typestate.type_checkAssignableFrom(location, value)) {
      return (Integer) ((MortarDataValueConst) value).getInner();
    } else if (MortarStringTypestate.typestate.type_checkAssignableFrom(location, value)) {
      try {
        return Integer.parseInt((String) ((MortarDataValueConst) value).getInner());
      } catch (Exception ignored) {
      }
    }
    context.errors.add(
        new WrongType(location, new TSList<>(), value.toString(), "constant string or int"));
    return null;
  }

  public static String assertConstString(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarStringTypestate.typestate.type_assertAssignableFrom(
        context.errors, location, value)) {
      return null;
    }
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (String) ((MortarDataValueConst) value).getInner();
  }

  @Override
  public ROList<String> typestate_traceFields(
      EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, MortarTupleFieldstate> field : fields) {
      if (!(field.first instanceof String)) {
        continue;
      }
      out.add((String) field.first);
    }
    return out;
  }

  public ROPair<Integer, MortarTupleFieldstate> assertField(
      EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) {
      return null;
    }
    final Integer index = fieldLookup.getOpt(fieldKey);
    if (index == null) {
      context.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(index, fields.get(index).second);
  }

  @Override
  public EvaluateResult typestate_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final ROPair<Integer, MortarTupleFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuplefieldstate_constAsValue(context, location, value, field.first);
  }

  @Override
  public boolean type_checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataTypestate type, TSList<Object> path) {
    if (type instanceof ImmutableType) {
      type = ((ImmutableType) type).innerType;
    }
    if (!(type instanceof MortarTupleTypestate)) {
      errors.add(new WrongType(location, path, type.toString(), "record"));
      return false;
    }
    boolean bad = false;
    final TSSet otherKeys = new TSSet<>();
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldstate>> e :
        ((MortarTupleTypestate) type).fields) {
      otherKeys.add(e.first);
    }
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldstate>> field : fields) {
      final ROPair<Integer, MortarTupleFieldstate> otherField =
          ((MortarTupleTypestate) type).fields.getOpt(field.first);
      if (otherField == null) {
        errors.add(new MissingField(location, path, field.first));
        bad = true;
        continue;
      }
      otherKeys.remove(field.first);
      if (!field.second.second.tuple_fieldtype_assignmentCheckFieldAssignableFrom(
          errors, location, otherField.second, path.mut().add(field.first))) {
        bad = true;
      }
    }
    for (Object otherKey : otherKeys) {
      bad = true;
      errors.add(new ExtraField(location, path, otherKey));
    }
    return bad;
  }

  @Override
  public EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field0, MortarDeferredCode baseCode) {
    final ROPair<Integer, MortarTupleFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuplefieldstate_variableAsValue(context, location, baseCode, field.first);
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.simple(
        new MortarDataValueVariableStack(this),
        new MortarTargetCode(((MortarTargetModuleContext) context.target).transfer(data)));
  }

  public static EvaluateResult newTupleCode(
      EvaluationContext context, Location location, ROList<ROPair<Object, EvaluateResult>> data) {
    TSList<ROPair<Object, MortarTupleFieldstate>> fields = new TSList<>();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    ectx.recordEffect(new MortarTargetCode(new JavaBytecodeInstructionInt(ANEWARRAY)));
    for (int i = 0; i < data.size(); i++) {
      fields.add(
          new ROPair<>(
              data.get(i).first,
              ((MortarTupleFieldable) data.get(i).second.value).asTupleFieldstate()));
      ectx.recordEffect(ectx.record(data.get(i).second).consume(context, location));
      ectx.recordEffect(new MortarTargetCode(JavaBytecodeUtils.literalIntShortByte(i)));
      ectx.recordEffect(new MortarTargetCode(JavaBytecodeUtils.arrayStoreObj));
    }
    return ectx.build(new MortarDataValueVariableStack(new MortarTupleTypestate(fields)));
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType other) {
    TSList<ROPair<Object, EvaluateResult>> working = new TSList<>();
    final MortarTupleType other1 = (MortarTupleType) other;
    for (int i = 0; i < other1.fields.size(); i++) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      working.add(
          new ROPair<>(
              other1.fields.get(i).first,
              ectx.record(
                      typestate_varAccess(
                          context,
                          location,
                          new MortarDataValueConst(MortarPrimitiveAll.typeInt, i),
                          new MortarDeferredCodeDup()))
                  .castTo(context, location, other1.fields.get(i).second.tuplefield_asType())));
    }
    return newTupleCode(context, location, working);
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType other, Object value) {
    final Object[] value1 = (Object[]) value;
    final MortarTupleType other1 = (MortarTupleType) other;
    final Object[] out = new Object[other1.fields.size()];
    for (int i = 0; i < other1.fields.size(); i++) {
      out[i] =
          fields
              .get(i)
              .second
              .tuplefieldstate_constCastTo(
                  context, location, other1.fields.get(i).second.tuplefield_asType(), value1[i]);
    }
    return EvaluateResult.pure(other.type_constAsValue(out));
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    if (!(prototype instanceof MortarTupleTypestate)) {
      return false;
    }
    final MortarTupleTypestate other = (MortarTupleTypestate) prototype;
    if (other.fields.size() != fields.size()) {
      return false;
    }
    for (int i = 0; i < fields.size(); i++) {
      if (!fields.get(i).second.tuplefieldstate_canCastTo(other.fields.get(i).second)) {
        return false;
      }
    }
    for (Map.Entry<Object, Integer> e : fieldLookup) {
      if (!e.getValue().equals(other.fieldLookup.getOpt(e.getKey()))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public MortarDataType typestate_asType() {
    final TSList<ROPair<Object, MortarTupleField>> fields = new TSList<>();
    for (ROPair<Object, MortarTupleFieldstate> field : this.fields) {
      fields.add(new ROPair<>(field.first, field.second.tuplefieldstate_asType()));
    }
    return new MortarTupleType(fields);
  }

  @Override
  public MortarDataTypestate typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    if (!(other instanceof MortarTupleTypestate)) {
      context.errors.add(new GeneralLocationError(location, "Type mismatch unforking"));
      return null;
    }
    TSList<ROPair<Object, MortarTupleFieldstate>> unforkedFields = new TSList<>();
    boolean ok = true;
    for (int i = 0; i < fields.size(); i++) {
      final ROPair<Object, MortarTupleFieldstate> field = fields.get(i);
      final MortarTupleFieldstate unforkedField =
          field.second.tuplefieldstate_unfork(
              context, location, ((MortarTupleTypestate) other).fields.get(i), otherLocation);
      if (unforkedField == null) {
        ok = false;
      }
      unforkedFields.add(new ROPair<>(field.first, unforkedField));
    }
    for (ROPair<Object, MortarTupleFieldstate> fieldType : fields) {}
    if (!ok) {
      return null;
    }
    return new MortarTupleTypestate(unforkedFields);
  }

  @Override
  public MortarDataTypestate typestate_fork() {
    TSList<ROPair<Object, MortarTupleFieldstate>> fields = new TSList<>();
    for (ROPair<Object, MortarTupleFieldstate> field : this.fields) {
      fields.add(new ROPair<>(field.first, field.second.tuplefieldstate_fork()));
    }
    return new MortarTupleTypestate(fields);
  }

  @Override
  public boolean typestate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    final ROList<ROPair<Object, MortarTupleFieldstate>> otherFields =
        ((MortarTupleTypestate) other).fields;
    boolean ok = true;
    for (int i = 0; i < fields.size(); i++) {
      ok =
          ok
              && fields
                  .get(i)
                  .second
                  .tuplefieldstate_bindMerge(
                      context, location, otherFields.get(i).second, otherLocation);
    }
    return ok;
  }

  @Override
  public boolean typestate_triviallyAssignableTo(AlligatorusType type) {
    if (!(type instanceof MortarTupleTypestate)) {
      return false;
    }
    final ROList<ROPair<Object, MortarTupleFieldstate>> otherFields =
        ((MortarTupleTypestate) type).fields;
    if (otherFields.size() != fields.size()) {
      return false;
    }
    for (int i = 0; i < fields.size(); i++) {
      if (!fields.get(i).second.tuplefieldstate_triviallyAssignableTo(otherFields.get(i).second)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return MortarTupleType.DESC;
  }
}
