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
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstKey;

public class MortarObjectImplTypestate
        implements BuiltinAutoExportable, MortarDataTypestate {
  public final MortarObjectInnerType meta;
  private final ROMap<Object, MortarObjectFieldstate> fields;

  public MortarObjectImplTypestate(
      MortarObjectInnerType meta, ROMap<Object, MortarObjectFieldstate> fields) {
      this.meta = meta;
    this.fields = fields;
  }

  public static MortarObjectImplTypestate create(
      MortarObjectInnerType meta, ROMap<Object, MortarObjectFieldstate> fields) {
    final MortarObjectImplTypestate out = new MortarObjectImplTypestate(meta, fields);
    out.postInit();
    return out;
  }

  @Override
  public MortarDataTypestate typestate_fork() {
    TSMap<Object, MortarObjectFieldstate> forkedFields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectFieldstate> fieldType : fields) {
      forkedFields.put(fieldType.getKey(), fieldType.getValue().fieldstate_fork());
    }
    return MortarObjectImplTypestate.create(meta, forkedFields);
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
  public JavaBytecode typestate_castTo(
      EvaluationContext context,
      Location location,
      MortarDataType prototype,
      MortarDeferredCode code) {
    return code.consume();
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    if (!(prototype instanceof MortarObjectImplType)) {
      return false;
    }
    return meta.canCastTo(((MortarObjectImplType) prototype).meta);
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
  public EvaluateResult typestate_variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field0) {
    final ROPair<Object, MortarObjectFieldstate> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.fieldstate_variableObjectFieldAsValue(context, location, base);
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
  public JavaDataDescriptor typestate_jvmDesc() {
    return JavaDataDescriptor.fromJVMName(meta.name.asInternalName());
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
