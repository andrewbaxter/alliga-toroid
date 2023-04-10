package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;

public class MortarPrimitiveAll implements MortarDataType, MortarDataTypestate {
  public static final MortarPrimitiveAll typeByte =
      new MortarPrimitiveAll(MortarPrimitiveAllByte.instance);
  public static final MortarPrimitiveAll typeBool =
      new MortarPrimitiveAll(MortarPrimitiveAllBool.instance);
  public static final MortarPrimitiveAll typeInt =
      new MortarPrimitiveAll(MortarPrimitiveAllInt.instance);
  public static final MortarPrimitiveAll typeString =
      new MortarPrimitiveAll(MortarPrimitiveAllString.instance);
  public static final MortarPrimitiveAll typeBytes =
      new MortarPrimitiveAll(MortarPrimitiveAllBytes.instance);

  private MortarPrimitiveAll(Inner inner) {
    this.inner = inner;
  }

  public EvaluateResult set(
      EvaluationContext context,
      Location location,
      JavaBytecode base,
      Value value,
      JavaBytecode setField) {
    if (!value.canCastTo(this)) {
      context.errors.add(new GeneralLocationError(location, "RHS can't be cast to LHS"));
      return EvaluateResult.error;
    }
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    ectx.recordPre(new MortarTargetCode(base));
    ectx.recordPre(
        ectx.record(ectx.record(value.castTo(context, location, this)).vary(context, location))
            .consume(context, location));
    ectx.recordPre(new MortarTargetCode(setField));
    return ectx.build(NullValue.value);
  }

  @Override
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return new MortarDataBinding(key, this, catchKey);
  }

  public interface Inner {
    JavaDataDescriptor jvmDesc();

    JavaBytecode returnBytecode();

    JavaBytecode storeBytecode(JavaBytecodeBindingKey key);

    JavaBytecode loadBytecode(JavaBytecodeBindingKey key);

    JavaBytecode arrayLoadBytecode();

    JavaBytecode arrayStoreBytecode();

    JavaBytecode literalBytecode(Object constData);
  }

  public final Inner inner;

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return inner.jvmDesc();
  }

  @Override
  public Value type_stackAsValue(JavaBytecode code) {
    return new MortarDataValueVariableStack(this, new MortarDeferredCodeStack(code));
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return inner.returnBytecode();
  }

  @Override
  public Value type_constAsValue(Object data) {
    return MortarDataValueConst.create(this, data);
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return inner.jvmDesc();
  }

  @Override
  public JavaBytecode typestate_returnBytecode() {
    return inner.returnBytecode();
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return inner.storeBytecode(key);
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return inner.loadBytecode(key);
  }

  @Override
  public JavaBytecode typestate_arrayStoreBytecode() {
    return inner.arrayStoreBytecode();
  }

  @Override
  public JavaBytecode typestate_arrayLoadBytecode() {
    return inner.arrayLoadBytecode();
  }

  @Override
  public EvaluateResult typestate_vary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(
        new MortarDataValueVariableStack(
            this, new MortarDeferredCodeStack(inner.literalBytecode(data))));
  }

  @Override
  public JavaBytecode typestate_castTo(
      EvaluationContext context, Location location, MortarDataType type, MortarDeferredCode code) {
    return code.consume();
  }

  public boolean triviallyAssignableTo(AlligatorusType type) {
    return type == this;
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType type) {
    return triviallyAssignableTo(type);
  }

  @Override
  public MortarDataType typestate_asType() {
    return this;
  }
}
