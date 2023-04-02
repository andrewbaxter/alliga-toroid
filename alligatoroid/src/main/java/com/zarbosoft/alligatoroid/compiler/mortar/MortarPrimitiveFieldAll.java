package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class MortarPrimitiveFieldAll implements MortarObjectField, MortarObjectFieldstateData {
  private final MortarObjectInnerType parentType;
  private final String name;
  public final MortarPrimitiveAll data;

  public MortarPrimitiveFieldAll(
      MortarObjectInnerType parentType, String name, MortarPrimitiveAll data) {
    this.parentType = parentType;
    this.name = name;
    this.data = data;
  }

  @Override
  public MortarObjectFieldstate fieldstate_fork() {
    return this;
  }

  @Override
  public MortarObjectField fieldstate_asField() {
    return this;
  }

  @Override
  public MortarObjectFieldstate field_newFieldstate() {
    return this;
  }

  @Override
  public EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base) {
    return EvaluateResult.pure(
        new MortarDataValueVariableStack(
            data,
            new MortarDeferredCodeAccessObjectField(
                base, parentType.name.asInternalName(), name, data.inner.jvmDesc())));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(
        MortarDataValueConst.create(data, uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  public JavaBytecode fieldstate_consume(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return consume(context, location, parentCode);
  }

  private JavaBytecode consume(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return JavaBytecodeUtils.seq()
        .add(parentCode.consume())
        .add(
            JavaBytecodeUtils.accessField(
                context.sourceLocation(location),
                parentType.name.asInternalName(),
                name,
                data.inner.jvmDesc()));
  }

  @Override
  public MortarDataType fieldstate_asType() {
    return data;
  }

  @Override
  public JavaBytecode fieldstate_storeBytecode(JavaBytecodeBindingKey key) {
    return data.inner.storeBytecode(key);
  }

  @Override
  public MortarDataTypestate fieldstate_newTypestate() {
    return data;
  }

  @Override
  public EvaluateResult fieldstate_variableValueAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult fieldstate_set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    return set(context, location, base, value);
  }

  private EvaluateResult set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    return data.set(
        context,
        location,
        base,
        value,
        JavaBytecodeUtils.setField(
            context.sourceLocation(location),
            parentType.name.asInternalName(),
            name,
            data.inner.jvmDesc()));
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
    return data.triviallyAssignableTo(type);
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    if (!(field instanceof MortarPrimitiveFieldAll)) {
      return false;
    }
    return data.triviallyAssignableTo(((MortarPrimitiveFieldAll) field).data);
  }
}
