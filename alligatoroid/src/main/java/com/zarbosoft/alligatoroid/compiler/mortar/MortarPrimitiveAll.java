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
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;

public class MortarPrimitiveAll implements MortarDataTypeForGeneric, MortarDataTypestate {
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
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return new MortarDataGenericBindingVar(key, this);
  }

  @Override
  public MortarRecordFieldstate asTupleFieldstate(int offset) {
    return new MortarDataGenericTupleFieldstate(offset, this);
  }

  public interface Inner {
    JavaDataDescriptor jvmDesc();

    JavaBytecode returnBytecode();

    JavaBytecode storeBytecode(JavaBytecodeBindingKey key);

    JavaBytecode loadBytecode(JavaBytecodeBindingKey key);

    JavaBytecode arrayLoadBytecode();

    JavaBytecode arrayStoreBytecode();

    JavaBytecode literalBytecode(Object constData);

    JavaBytecode fromObj();

    JavaBytecode toObj();
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
    return new MortarDataGenericField(parentType, fieldName, this);
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return inner.loadBytecode(key);
  }

  @Override
  public JavaBytecode typestate_jvmToObj() {
    return inner.toObj();
  }

  @Override
  public JavaBytecode typestate_jvmFromObj() {
    return inner.fromObj();
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return inner.storeBytecode(key);
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
  public boolean typestate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation) {
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
