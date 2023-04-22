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
import com.zarbosoft.rendaw.common.ROPair;

public class MortarPrimitiveAll implements MortarDataTypeForGeneric, MortarDataTypestateForGeneric {
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

  private MortarPrimitiveAll(Info info) {
    this.info = info;
  }

  @Override
  public ROPair<JavaBytecodeBindingKey, Binding> type_newInitialBinding() {
    final JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    return new ROPair<>(key, new MortarDataGenericBindingVar(key, this));
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return new MortarDataGenericRecordField(offset, this);
  }

  public interface Info {
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

  public final Info info;

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return info.jvmDesc();
  }

  @Override
  public Value type_stackAsValue() {
    return new MortarDataValueVariableStack(this);
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return info.returnBytecode();
  }

  @Override
  public Value type_constAsValue(Object data) {
    return new MortarDataValueConst(this, data);
  }

  @Override
  public MortarDataTypestateForGeneric type_newTypestate() {
    return this;
  }

  @Override
  public MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName) {
    return new MortarDataGenericField(parentType, fieldName, this);
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return info.loadBytecode(key);
  }

  @Override
  public JavaBytecode typestate_jvmToObj() {
    return info.toObj();
  }

  @Override
  public JavaBytecode typestate_jvmFromObj() {
    return info.fromObj();
  }

  @Override
  public Object typestate_constConsume(EvaluationContext context, Location id, Object value) {
    return value;
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return info.storeBytecode(key);
  }

  @Override
  public EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.simple(
        new MortarDataValueVariableStack(this), new MortarTargetCode(info.literalBytecode(data)));
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType type) {
    return EvaluateResult.pure(type.type_stackAsValue());
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarType type, Object value) {
    return EvaluateResult.pure(new MortarDataValueConst(this, value));
  }

  public boolean triviallyAssignableTo(AlligatorusType type) {
    return type == this;
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType type) {
    return triviallyAssignableTo(type);
  }

  @Override
  public MortarDataTypeForGeneric typestate_asType() {
    return this;
  }

  @Override
  public MortarDataTypestateForGeneric typestate_unfork(
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
  public MortarDataTypestateForGeneric typestate_fork() {
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
    return info.jvmDesc();
  }
}
