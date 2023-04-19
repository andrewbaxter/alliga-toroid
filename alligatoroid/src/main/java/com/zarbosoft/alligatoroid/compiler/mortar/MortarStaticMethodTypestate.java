package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeLineNumber;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.Assertion;

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;

/**
 * Represents a StaticMethodMeta object which when called at const time generates runtime code to
 * call the static method.
 */
public class MortarStaticMethodTypestate
    implements BuiltinSingletonExportable, MortarDataTypestateForGeneric, MortarDataTypeForGeneric {
  // TODO move method type info into the type, check during type check
  public static final MortarStaticMethodTypestate typestate = new MortarStaticMethodTypestate();
  public static final JavaDataDescriptor DESC =
      JavaDataDescriptor.fromObjectClass(StaticMethodMeta.class);

  private MortarStaticMethodTypestate() {}

  @Override
  public EvaluateResult typestate_constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    final StaticMethodMeta meta = (StaticMethodMeta) inner;
    if (meta.definitionSet != null) {
      ((MortarTargetModuleContext) context.target).dependencies.add(meta.definitionSet);
    }
    final StaticAutogen.FuncInfo funcInfo = meta.funcInfo;
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, code, argument)) {
      return EvaluateResult.error;
    }
    code.add(JavaBytecodeLineNumber.create(context.sourceLocation(location)));
    code.add(
        JavaBytecodeUtils.callStaticMethod(
            -1,
            funcInfo.base.asInternalName(),
            funcInfo.name,
            JavaMethodDescriptor.fromParts(
                funcInfo.returnType.type_jvmDesc(), funcInfo.argDescriptor())));
    if (funcInfo.returnType == nullType) {
      return EvaluateResult.simple(NullValue.value, new MortarTargetCode(code));
    } else {
      return EvaluateResult.simple(
          funcInfo.returnType.type_stackAsValue(), new MortarTargetCode(code));
    }
  }

  @Override
  public EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType prototype) {
    throw new Assertion();
  }

  @Override
  public EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType type, Object value) {
    return EvaluateResult.pure(new MortarDataValueConst(this, value));
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    return prototype == this;
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
    return DESC;
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
    return JavaBytecodeUtils.cast(DESC);
  }

  @Override
  public Object typestate_constConsume(EvaluationContext context, Location id, Object value) {
    return value;
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

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return DESC;
  }

  @Override
  public Value type_stackAsValue() {
    return new MortarDataValueVariableStack(this);
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public Value type_constAsValue(Object data) {
    return MortarDataValueConst.create(this, data);
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
  public Binding type_newInitialBinding(JavaBytecodeBindingKey key) {
    return new MortarDataGenericBindingVar(key, this);
  }

  @Override
  public MortarRecordField newTupleField(int offset) {
    return new MortarDataGenericTupleField(offset, this);
  }

  public static class ConvertImmediateArgRootRes {
    public final boolean isError;
    public final Object[] args;

    public ConvertImmediateArgRootRes(boolean isError, Object[] args) {
      this.isError = isError;
      this.args = args;
    }
  }
}
