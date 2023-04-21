package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstructionInt;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeDup;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.objectweb.asm.Opcodes.ANEWARRAY;

public class MortarRecordTypestate implements BuiltinAutoExportable, MortarDataTypestateForGeneric {
  public final ROList<ROPair<Object, MortarRecordFieldstate>> fields;
  public final ROMap<Object, Integer> fieldLookup;

  public MortarRecordTypestate(ROList<ROPair<Object, MortarRecordFieldstate>> fields) {
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

  @Override
  public JavaBytecode typestate_jvmToObj() {
    return null;
  }

  @Override
  public JavaBytecode typestate_jvmFromObj() {
    return JavaBytecodeUtils.cast(MortarRecordType.DESC);
  }

  @Override
  public Object typestate_constConsume(EvaluationContext context, Location id, Object value) {
    return value;
  }

  public static Object assertConstKey(EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    final AlligatorusType type = value.type(context);
    if (type != MortarPrimitiveAll.typeString && type != MortarPrimitiveAll.typeInt) {
      return null;
    }
    if (!(value instanceof MortarDataValueConst)) {
      context.errors.add(new ValueNotWhole(location));
      return null;
    }
    return ((MortarDataValueConst) value).getInner();
  }

  public static String assertConstString(
      EvaluationContext context, Location location, Value value) {
    if (!(value instanceof MortarDataValue)) {
      context.errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
      return null;
    }
    if (((MortarDataValue) value).type(context) != MortarPrimitiveAll.typeString) {
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
    for (ROPair<Object, MortarRecordFieldstate> field : fields) {
      if (!(field.first instanceof String)) {
        continue;
      }
      out.add((String) field.first);
    }
    return out;
  }

  public ROPair<Integer, MortarRecordFieldstate> assertField(
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
    final ROPair<Integer, MortarRecordFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.recordfieldstate_constAsValue(context, location, value, field.first);
  }

  @Override
  public EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field0, MortarDeferredCode baseCode) {
    final ROPair<Integer, MortarRecordFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.recordfieldstate_variableAsValue(context, location, baseCode, field.first);
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
    TSList<ROPair<Object, MortarRecordFieldstate>> fields = new TSList<>();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    ectx.recordEffect(new MortarTargetCode(new JavaBytecodeInstructionInt(ANEWARRAY)));
    TSList<Value> usedValues = new TSList<>();
    for (int i = 0; i < data.size(); i++) {
      final AlligatorusType elType = data.get(i).second.value.type(context);
      final Value usedValue = ectx.record(data.get(i).second);
      ectx.recordEffect(usedValue.consume(context, location));
      usedValues.add(usedValue);
      ectx.recordEffect(new MortarTargetCode(JavaBytecodeUtils.literalIntShortByte(i)));
      ectx.recordEffect(new MortarTargetCode(Global.JBC_ARRAY_STORE_OBJ));
      fields.add(
          new ROPair<>(
              data.get(i).first,
              ((MortarRecordFieldable) elType).newTupleField(i).recordfield_newFieldstate()));
    }
    for (Value value : new ReverseIterable<>(usedValues)) {
      ectx.recordEffect(value.cleanup(context, location));
    }
    return ectx.build(new MortarDataValueVariableStack(new MortarRecordTypestate(fields)));
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType other) {
    TSList<ROPair<Object, EvaluateResult>> working = new TSList<>();
    final MortarRecordType other1 = (MortarRecordType) other;
    for (int i = 0; i < other1.fields.size(); i++) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      working.add(
          new ROPair<>(
              other1.fields.get(i).first,
              ectx.build(
                  ectx.record(
                      ectx.record(
                              typestate_varAccess(
                                  context,
                                  location,
                                  new MortarDataValueConst(MortarPrimitiveAll.typeInt, i),
                                  new MortarDeferredCodeDup()))
                          .castTo(
                              context,
                              location,
                              other1.fields.get(i).second.recordfield_asType())))));
    }
    return newTupleCode(context, location, working);
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType other, Object value) {
    final Object[] value1 = (Object[]) value;
    final MortarRecordType other1 = (MortarRecordType) other;
    final Object[] out = new Object[other1.fields.size()];
    for (int i = 0; i < other1.fields.size(); i++) {
      out[i] =
          fields
              .get(i)
              .second
              .recordfieldstate_constCastTo(
                  context, location, other1.fields.get(i).second.recordfield_asType(), value1[i]);
    }
    return EvaluateResult.pure(other.type_constAsValue(out));
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    if (!(prototype instanceof MortarRecordTypestate)) {
      return false;
    }
    final MortarRecordTypestate other = (MortarRecordTypestate) prototype;
    if (other.fields.size() != fields.size()) {
      return false;
    }
    for (int i = 0; i < fields.size(); i++) {
      if (!fields
          .get(i)
          .second
          .recordfieldstate_canCastTo(other.fields.get(i).second.recordfieldstate_asType())) {
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
  public MortarDataTypeForGeneric typestate_asType() {
    return asType();
  }

  @NotNull
  private MortarRecordType asType() {
    final TSList<ROPair<Object, MortarRecordField>> fields = new TSList<>();
    for (ROPair<Object, MortarRecordFieldstate> field : this.fields) {
      fields.add(new ROPair<>(field.first, field.second.recordfieldstate_asField()));
    }
    return new MortarRecordType(fields);
  }

  @Override
  public MortarDataTypestateForGeneric typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    if (!(other instanceof MortarRecordTypestate)) {
      context.errors.add(new GeneralLocationError(location, "Type mismatch unforking"));
      return null;
    }
    TSList<ROPair<Object, MortarRecordFieldstate>> unforkedFields = new TSList<>();
    boolean ok = true;
    for (int i = 0; i < fields.size(); i++) {
      final ROPair<Object, MortarRecordFieldstate> field = fields.get(i);
      final MortarRecordFieldstate unforkedField =
          field.second.recordfieldstate_unfork(
              context,
              location,
              ((MortarRecordTypestate) other).fields.get(i).second,
              otherLocation);
      if (unforkedField == null) {
        ok = false;
      }
      unforkedFields.add(new ROPair<>(field.first, unforkedField));
    }
    for (ROPair<Object, MortarRecordFieldstate> fieldType : fields) {}
    if (!ok) {
      return null;
    }
    return new MortarRecordTypestate(unforkedFields);
  }

  @Override
  public MortarDataTypestateForGeneric typestate_fork() {
    TSList<ROPair<Object, MortarRecordFieldstate>> fields = new TSList<>();
    for (ROPair<Object, MortarRecordFieldstate> field : this.fields) {
      fields.add(new ROPair<>(field.first, field.second.recordfieldstate_fork()));
    }
    return new MortarRecordTypestate(fields);
  }

  @Override
  public boolean typestate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    final ROList<ROPair<Object, MortarRecordFieldstate>> otherFields =
        ((MortarRecordTypestate) other).fields;
    boolean ok = true;
    for (int i = 0; i < fields.size(); i++) {
      ok =
          ok
              && fields
                  .get(i)
                  .second
                  .recordfieldstate_bindMerge(
                      context, location, otherFields.get(i).second, otherLocation);
    }
    return ok;
  }

  @Override
  public boolean typestate_triviallyAssignableTo(AlligatorusType type) {
    if (!(type instanceof MortarRecordTypestate)) {
      return false;
    }
    final ROList<ROPair<Object, MortarRecordFieldstate>> otherFields =
        ((MortarRecordTypestate) type).fields;
    if (otherFields.size() != fields.size()) {
      return false;
    }
    for (int i = 0; i < fields.size(); i++) {
      if (!fields.get(i).second.recordfieldstate_triviallyAssignableTo(otherFields.get(i).second)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return MortarRecordType.DESC;
  }
}
