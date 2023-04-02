package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class MortarObjectImplFieldstate implements MortarObjectFieldstateData {
  public final MortarObjectInnerType info;
  private final ROMap<Object, MortarObjectFieldstate> fields;
  private final MortarObjectInnerType parentInfo;
  private final String name;

  public MortarObjectImplFieldstate(
      MortarObjectInnerType parentInfo,
      String name,
      MortarObjectInnerType info,
      ROMap<Object, MortarObjectFieldstate> fields) {
    this.parentInfo = parentInfo;
    this.name = name;
    this.info = info;
    this.fields = fields;
  }

  @Override
  public MortarObjectFieldstate fieldstate_fork() {
    TSMap<Object, MortarObjectFieldstate> forkedFields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectFieldstate> fieldType : fields) {
      forkedFields.put(fieldType.getKey(), fieldType.getValue().fieldstate_fork());
    }
    return new MortarObjectImplFieldstate(parentInfo, name, info, forkedFields);
  }

  @Override
  public MortarObjectField fieldstate_asField() {
    TSMap<Object, MortarObjectField> fields = new TSMap<>();
    for (Map.Entry<Object, MortarObjectFieldstate> field : this.fields) {
      fields.put(field.getKey(), field.getValue().fieldstate_asField());
    }
    return new MortarObjectImplField(parentInfo, name, info, fields);
  }

  @Override
  public EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base) {
    return EvaluateResult.pure(
        new MortarDataValueVariableStack(
            newTypestate(),
            new MortarDeferredCodeAccessObjectField(
                base, parentInfo.name.asInternalName(), name, jvmDesc())));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(
        MortarDataValueConst.create(
            newTypestate(), uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  public EvaluateResult fieldstate_set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    MortarObjectImplType currentType = asType();
    if (!value.canCastTo(currentType)) {
      context.errors.add(new GeneralLocationError(location, "RHS can't be cast to LHS"));
      return EvaluateResult.error;
    }
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    ectx.recordPre(new MortarTargetCode(base));
    ectx.recordPre(
        ectx.record(
                ectx.record(value.castTo(context, location, currentType)).vary(context, location))
            .consume(context, location));
    ectx.recordPre(
        new MortarTargetCode(
            JavaBytecodeUtils.setField(
                context.sourceLocation(location),
                parentInfo.name.asInternalName(),
                name,
                jvmDesc())));
    return ectx.build(NullValue.value);
  }

  private MortarObjectImplType asType() {
    return new MortarObjectImplType(
        info,
        TSMap.createWith(
            m -> {
              for (Map.Entry<Object, MortarObjectFieldstate> field : fields) {
                m.put(field.getKey(), field.getValue().fieldstate_asField());
              }
            }));
  }

  private MortarDataTypestate newTypestate() {
    return new MortarObjectImplTypestate(
        info,
        TSMap.createWith(
            m -> {
              for (Map.Entry<Object, MortarObjectFieldstate> field : fields) {
                m.put(field.getKey(), field.getValue().fieldstate_fork());
              }
            }));
  }

  private JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.fromJVMName(info.name.asInternalName());
  }

  private JavaBytecode consume(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return JavaBytecodeUtils.seq()
        .add(parentCode.consume())
        .add(
            JavaBytecodeUtils.accessField(
                context.sourceLocation(location),
                parentInfo.name.asInternalName(),
                name,
                jvmDesc()));
  }

  @Override
  public JavaBytecode fieldstate_castTo(
      EvaluationContext context,
      Location location,
      MortarDataType prototype,
      MortarDeferredCode parentCode) {
    return consume(context, location, parentCode);
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    if (!(type instanceof MortarObjectImplType)) {
      return false;
    }
    return triviallyAssignableTo(
        ((MortarObjectImplType) type).meta, ((MortarObjectImplType) type).fields);
  }

  public boolean triviallyAssignableTo(
      MortarObjectInnerType other, ROMap<Object, MortarObjectField> fields) {
    // Is subclass
    if (!info.canCastTo(other)) {
      return false;
    }
    for (Map.Entry<Object, MortarObjectFieldstate> field : this.fields) {
      final MortarObjectField otherField = fields.getOpt(field.getKey());
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
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    if (!(field instanceof MortarObjectImplField)) {
      return false;
    }
    return triviallyAssignableTo(
        ((MortarObjectImplField) field).info, ((MortarObjectImplField) field).fields);
  }

  @Override
  public JavaBytecode fieldstate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public MortarDataTypestate fieldstate_newTypestate() {
    return newTypestate();
  }

  @Override
  public JavaBytecode fieldstate_consume(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return consume(context, location, parentCode);
  }

  @Override
  public MortarDataType fieldstate_asType() {
    return asType();
  }
}
