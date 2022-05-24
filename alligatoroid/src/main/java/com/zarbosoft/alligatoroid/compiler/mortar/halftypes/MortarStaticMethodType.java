package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.StaticMethodMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinArtifact;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class MortarStaticMethodType extends MortarBaseObjectType implements SingletonBuiltinArtifact {
  // TODO move method type info into the type, check during type check
  public static final MortarStaticMethodType type = new MortarStaticMethodType();
  public static final JavaDataDescriptor DESC =
      JavaDataDescriptor.fromObjectClass(StaticMethodMeta.class);

  private MortarStaticMethodType() {}

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public EvaluateResult constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    final StaticMethodMeta meta = (StaticMethodMeta) inner;
    if (meta.definitionSet != null)
      ((MortarTargetModuleContext) context.target)
          .dependencies.add( meta.definitionSet);
    final Meta.FuncInfo funcInfo = meta.funcInfo;
    JavaBytecodeSequence pre = new JavaBytecodeSequence();
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    JavaBytecodeSequence post = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, pre, code, post, argument)) {
      return EvaluateResult.error;
    }
    code.line(context.sourceLocation(location));
    code.add(
        JavaBytecodeUtils.callStaticMethod(
            -1,
            funcInfo.base.asInternalName(),
            funcInfo.name,
            JavaMethodDescriptor.fromParts(
                funcInfo.returnType.jvmDesc(), funcInfo.argDescriptor())));
    if (funcInfo.returnType == nullType)
      return new EvaluateResult(
          new MortarTargetCode(pre.add(code)), new MortarTargetCode(post), nullValue);
    else
      return new EvaluateResult(
          new MortarTargetCode(pre),
          new MortarTargetCode(post),
          funcInfo.returnType.stackAsValue(code));
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return DESC;
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
