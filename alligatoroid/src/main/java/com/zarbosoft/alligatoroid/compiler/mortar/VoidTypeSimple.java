package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VoidValue;
import com.zarbosoft.rendaw.common.ROPair;

public abstract class VoidTypeSimple
    implements VoidType, VoidTypestate, AutoExportable, MortarRecordField, MortarRecordFieldstate {
  @Override
  public AlligatorusType typestate_asType() {
    return this;
  }

  @Override
  public EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public VoidTypestate type_newTypestate() {
    return this;
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return this;
  }

  @Override
  public MortarRecordFieldstate recordfield_newFieldstate() {
    return this;
  }

  @Override
  public AlligatorusType recordfield_asType() {
    return this;
  }

  @Override
  public EvaluateResult recordfieldstate_constAsValue(
      EvaluationContext context, Location location, Object[] data) {
    return EvaluateResult.pure(new VoidValue(this));
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_fork() {
    return this;
  }

  @Override
  public EvaluateResult recordfieldstate_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode) {
    return EvaluateResult.simple(new VoidValue(this), new MortarTargetCode(baseCode.drop()));
  }

  @Override
  public MortarRecordField recordfieldstate_asField() {
    return this;
  }

  @Override
  public Object recordfieldstate_constCastTo(
      EvaluationContext context, Location location, AlligatorusType other, Object value) {
    return value;
  }

  @Override
  public AlligatorusType recordfieldstate_asType() {
    return this;
  }

  @Override
  public ROPair<JavaBytecodeBindingKey, Binding> type_newInitialBinding() {
    return new ROPair<>(null, new SimpleBinding(new VoidValue(this)));
  }
}
