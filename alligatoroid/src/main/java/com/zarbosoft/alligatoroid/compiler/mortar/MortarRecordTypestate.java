package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
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
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

public class MortarRecordTypestate
        implements BuiltinAutoExportable, MortarDataTypestate {
  public final TSOrderedMap<Object, ROPair<Integer, MortarTupleFieldType>> fields;

  public MortarRecordTypestate(TSOrderedMap<Object, ROPair<Integer, MortarTupleFieldType>> fields) {
    this.fields = fields;
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
    context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "constant string or int"));
    return null;
  }

  public static String assertConstString(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (!MortarStringTypestate.typestate.type_assertAssignableFrom(context.errors, location, value)) {
      return null;
    }
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return (String) ((MortarDataValueConst) value).getInner();
  }

  @Override
  public ROList<String> typestate_traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> field : fields) {
      if (!(field.first instanceof String)) {
        continue;
      }
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
            for (ROPair<Object, MortarDataTypestate> field : fields) {
              //
            }
            for (ROPair<SemiserialSubvalue, SemiserialSubvalue> datum : s.data) {}
          }
        });
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return MortarTupleTypestate.DESC;
  }

  public ROPair<Integer, MortarTupleFieldType> assertField(EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) {
      return null;
    }
    final ROPair<Integer, MortarTupleFieldType> field = fields.getOpt(fieldKey);
    if (field == null) {
      context.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(field.first, field.second);
  }

  @Override
  public EvaluateResult typestate_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuple_fieldtype_constAsValue(context,location,value, field.first);
  }

  @Override
  public boolean type_checkAssignableFrom(
          TSList<Error> errors, Location location, MortarDataTypestate type, TSList<Object> path) {
    if (type instanceof ImmutableType) {
      type = ((ImmutableType) type).innerType;
    }
    if (!(type instanceof MortarRecordTypestate)) {
      errors.add(new WrongType(location, path, type.toString(), "record"));
      return false;
    }
    boolean bad = false;
    final TSSet otherKeys = new TSSet<>();
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> e : ((MortarRecordTypestate) type).fields) {
      otherKeys.add(e.first);
    }
    for (ROPair<Object, ROPair<Integer, MortarTupleFieldType>> field : fields) {
      final ROPair<Integer, MortarTupleFieldType> otherField = ((MortarRecordTypestate) type).fields.getOpt(field.first);
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
  public EvaluateResult typestate_variableValueAccess(EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    final ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuple_fieldtype_variableAsValue(context,location,base, field.first);
  }

  @Override
  public JavaBytecode typestate_arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadObj;
  }

  @Override
  public JavaBytecode typestate_arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreObj;
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  @Override
  public JavaBytecode typestate_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public EvaluateResult typestate_vary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(
        typestate_stackAsValue(((MortarTargetModuleContext) context.target).transfer((Exportable) data)));
  }
}
