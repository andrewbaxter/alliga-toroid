package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeLineNumber;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
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
    implements BuiltinSingletonExportable, MortarDataTypestate, MortarDataType {
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
    JavaBytecodeSequence pre = new JavaBytecodeSequence();
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, pre, code, argument)) {
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
      return EvaluateResult.simple(NullValue.value, new MortarTargetCode(pre.add(code)));
    } else {
      return EvaluateResult.simple(
          funcInfo.returnType.type_stackAsValue(code), new MortarTargetCode(pre));
    }
  }

  @Override
  public JavaBytecode typestate_castTo(
      EvaluationContext context,
      Location location,
      MortarDataType prototype,
      MortarDeferredCode code) {
    throw new Assertion();
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {}

  @Override
  public MortarDataType typestate_asType() {
    return this;
  }

  @Override
  public boolean typestate_varBindMerge(
      EvaluationContext context, Location location, Binding other) {
    return true;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return DESC;
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
        typestate_stackAsValue(
            ((MortarTargetModuleContext) context.target).transfer((Exportable) data)));
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return DESC;
  }

  @Override
  public Value type_stackAsValue(JavaBytecode code) {
    return new MortarDataValueVariableStack(this, new MortarDeferredCodeStack(code));
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
  public Binding type_newInitialBinding(
      JavaBytecodeBindingKey key, JavaBytecodeCatchKey finallyKey) {
    return new MortarDataBinding(key, this, finallyKey);
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
