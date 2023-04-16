package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;

import static com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils.pop;

public class MortarPrimitiveAll
    implements MortarDataType, MortarDataTypestate, MortarDataBindstate {
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

  @Override
  public Binding type_newInitialBinding(
      JavaBytecodeBindingKey key, JavaBytecodeCatchKey finallyKey) {
    return new MortarDataVarBinding(key, this, finallyKey);
  }

  @Override
  public Value bindstate_constAsValue(Object value) {
    return MortarDataValueConst.create(this, value);
  }

  @Override
  public MortarDataTypestate bindstate_load() {
    return this;
  }

  @Override
  public MortarDataBindstate bindstate_fork() {
    return this;
  }

  @Override
  public boolean bindstate_bindMerge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return true;
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
  public Value type_stackAsValue() {
    return new MortarDataValueVariableStack(this);
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
  public MortarDataTypestate type_newTypestate() {
  return this;
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
  return new MortarDataGenericField(parentType,fieldName,this);
  }

  @Override
  public JavaBytecode bindstate_storeBytecode(JavaBytecodeBindingKey key) {
    return inner.storeBytecode(key);
  }

  @Override
  public JavaBytecode bindstate_loadBytecode(JavaBytecodeBindingKey key) {
    return inner.loadBytecode(key);
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return inner.storeBytecode(key);
  }

  @Override
  public MortarDataBindstate typestate_newBinding() {
    return this;
  }

  @Override
  public EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.simple(
        new MortarDataValueVariableStack(this), new MortarTargetCode(inner.literalBytecode(data)));
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType type) {
    return EvaluateResult.pure(type.type_stackAsValue());
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType type, Object value) {
    return EvaluateResult.pure(MortarDataValueConst.create(this, value));
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

  @Override
  public MortarDataTypestate typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
    if (other != this) {
      context.errors.add(new GeneralLocationError(location, "Type mismatch at branch merge"));
      return null;
    }
    return this;
  }

  @Override
  public MortarDataTypestate typestate_fork() {
  return this;
  }

  @Override
  public boolean typestate_bindMerge(EvaluationContext context, Location location, MortarDataTypestate other, Location otherLocation) {
  return true;
  }

  @Override
  public boolean typestate_triviallyAssignableTo(AlligatorusType type) {
  return type == this;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
  return inner.jvmDesc();
  }
}
