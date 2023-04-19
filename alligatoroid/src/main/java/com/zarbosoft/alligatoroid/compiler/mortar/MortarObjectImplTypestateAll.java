package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstKey;

public class MortarObjectImplTypestateAll implements BuiltinAutoExportable, MortarDataTypestate {
  public final MortarObjectInnerType meta;
  private final ROMap<Object, MortarObjectFieldstate> fields;

  public MortarObjectImplTypestateAll(
      MortarObjectInnerType meta, ROMap<Object, MortarObjectFieldstate> fields) {
    this.meta = meta;
    this.fields = fields;
  }

  public static MortarObjectImplTypestateAll create(
      MortarObjectInnerType meta, ROMap<Object, MortarObjectFieldstate> fields) {
    final MortarObjectImplTypestateAll out = new MortarObjectImplTypestateAll(meta, fields);
    out.postInit();
    return out;
  }

  @Override
  public ROList<String> typestate_traceFields(
      EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<Object, MortarObjectFieldstate> field : fields) {
      if (!(field.getKey() instanceof String)) {
        continue;
      }
      out.add((String) field.getKey());
    }
    return out;
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType type) {
    return EvaluateResult.pure(type.type_stackAsValue());
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType type, Object value) {
    return EvaluateResult.pure(type.type_constAsValue(value));
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType type) {
    if (!(type instanceof MortarObjectImplType)) {
      return false;
    }
    final MortarObjectImplType other = (MortarObjectImplType) type;
    if (!meta.canAssignTo(other.meta)) {
      return false;
    }
    for (Map.Entry<Object, MortarObjectFieldstate> field : fields) {
      final MortarObjectField otherField = other.fields.getOpt(field.getKey());
      if (otherField == null) {
      return false;
    }
    if (!field.getValue().fieldstate_canCastTo(otherField.field_asType())) {
      return false;
    }
    }
    return true;
  }

  public MortarDataType asType() {
    TSMap<Object, MortarObjectField> fields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectFieldstate> field : this.fields) {
      fields.put(field.getKey(), field.getValue().fieldstate_asField());
    }
    return new MortarObjectImplType(meta, fields);
  }

  @Override
  public MortarDataType typestate_asType() {
    return asType();
  }

  public static UnforkedRes unfork(
      EvaluationContext context,
      MortarObjectInnerType selfMeta,
      ROMap<Object, MortarObjectFieldstate> selfFields,
      Location location,
      MortarObjectInnerType otherMeta,
      ROMap<Object, MortarObjectFieldstate> otherFields,
      Location otherLocation) {
    final boolean useSelf;
    final ROMap<Object, MortarObjectFieldstate> useBaseFields;
    final Location useLocation;
    final ROMap<Object, MortarObjectFieldstate> useOtherFields;
    final Location useOtherLocation;
    if (otherMeta.canAssignTo(selfMeta)) {
      useSelf = true;
      useBaseFields = selfFields;
      useLocation = location;
      useOtherFields = otherFields;
      useOtherLocation = otherLocation;
    } else if (selfMeta.canAssignTo(otherMeta)) {
      useSelf = false;
      useBaseFields = otherFields;
      useLocation = otherLocation;
      useOtherFields = selfFields;
      useOtherLocation = location;
    } else {
      context.errors.add(new GeneralLocationError(location, "Type mismatch unforking"));
      return null;
    }
    TSMap<Object, MortarObjectFieldstate> unforkedFields = new TSMap<>();
    boolean ok = true;
    for (Map.Entry<Object, MortarObjectFieldstate> fieldType : useBaseFields) {
      final MortarObjectFieldstate unforkedField =
          fieldType
              .getValue()
              .fieldstate_unfork(
                  context, useLocation, useOtherFields.get(fieldType.getKey()), useOtherLocation);
      if (unforkedField == null) {
        ok = false;
      }
      unforkedFields.put(fieldType.getKey(), unforkedField);
    }
    if (!ok) {
      return null;
    }
    return new UnforkedRes(useSelf, unforkedFields);
  }

  @Override
  public MortarDataTypestate typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    if (!(other instanceof MortarObjectImplTypestateAll)) {
      context.errors.add(new GeneralLocationError(location, "Type mismatch unforking"));
      return null;
    }
    final UnforkedRes unforked =
        unfork(
            context,
            this.meta,
            this.fields,
            location,
            ((MortarObjectImplTypestateAll) other).meta,
            ((MortarObjectImplTypestateAll) other).fields,
            otherLocation);
    return MortarObjectImplTypestateAll.create(
        unforked.useSelf ? meta : ((MortarObjectImplTypestateAll) other).meta, unforked.fields);
  }

  @Override
  public MortarDataTypestate typestate_fork() {
    return fork();
  }

  @Override
  public boolean typestate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    boolean ok = true;
    for (Map.Entry<Object, MortarObjectFieldstate> fieldType : fields) {
      if (!fieldType
          .getValue()
          .fieldstate_varBindMerge(
              context,
              location,
              ((MortarObjectImplTypestateAll) other).fields.get(fieldType.getKey()),
              otherLocation)) {
        ok = false;
      }
    }
    return ok;
  }

  @Override
  public boolean typestate_triviallyAssignableTo(AlligatorusType other) {
    if (!(other instanceof MortarObjectImplType)) {
      return false;
    }
    // Is subclass
    if (!meta.canAssignTo(((MortarObjectImplType) other).meta)) {
      return false;
    }
    for (Map.Entry<Object, MortarObjectFieldstate> field : this.fields) {
      final MortarObjectField otherField =
          ((MortarObjectImplType) other).fields.getOpt(field.getKey());
      // Child has new fields; as subclass, this is okay
      if (otherField == null) {
        continue;
      }
      // Enforce restrictions (i.e. subclass where X must be even)
      if (!field.getValue().fieldstate_triviallyAssignableTo(otherField)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return meta.jvmDesc();
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
  return JavaBytecodeUtils.cast(meta.jvmDesc());
  }

  @Override
  public String toString() {
    return meta.name.toString();
  }

  public ROPair<Object, MortarObjectFieldstate> assertField(
      EvaluationContext context, Location location, Value field0) {
    final Object fieldKey = assertConstKey(context, location, field0);
    if (fieldKey == null) {
      return null;
    }
    final MortarObjectFieldstate field = fields.getOpt(fieldKey);
    if (field == null) {
      context.errors.add(new NoField(location, fieldKey));
      return null;
    }
    return new ROPair<>(fieldKey, field);
  }

  @Override
  public EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field0, MortarDeferredCode baseCode) {
    final ROPair<Object, MortarObjectFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.fieldstate_variableObjectFieldAsValue(context, location, baseCode);
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public EvaluateResult typestate_constValueAccess(
      EvaluationContext context, Location location, Object base, Value field0) {
    final ROPair<Object, MortarObjectFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.fieldstate_constObjectFieldAsValue(context, location, base);
  }

  @Override
  public EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.simple(
        new MortarDataValueVariableStack(this),
        new MortarTargetCode(
            ((MortarTargetModuleContext) context.target).transfer( data)));
  }

  public MortarObjectImplTypestateAll fork() {
    TSMap<Object, MortarObjectFieldstate> forkedFields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectFieldstate> fieldType : fields) {
      forkedFields.put(fieldType.getKey(), fieldType.getValue().fieldstate_fork());
    }
    return MortarObjectImplTypestateAll.create(meta, forkedFields);
  }

  @Override
  public MortarRecordFieldstate asTupleFieldstate(int offset) {
  return new MortarDataGenericTupleFieldstate(offset, this);
  }

  public static class UnforkedRes {
    public final TSMap<Object, MortarObjectFieldstate> fields;
    public final boolean useSelf;

    public UnforkedRes(boolean useSelf, TSMap<Object, MortarObjectFieldstate> fields) {
      this.useSelf = useSelf;
      this.fields = fields;
    }
  }
}
