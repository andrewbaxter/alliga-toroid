package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeLineNumber;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst.nullValue;

public class MortarStaticMethodTypestate extends MortarBaseObjectTypestate
    implements BuiltinSingletonExportable {
  // TODO move method type info into the type, check during type check
  public static final MortarStaticMethodTypestate type = new MortarStaticMethodTypestate();
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
    JavaBytecodeSequence post = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, pre, code, post, argument)) {
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
      return new EvaluateResult(
          new MortarTargetCode(pre.add(code)), new MortarTargetCode(post), nullValue, jumpValues, jumpValues);
    } else {
      return new EvaluateResult(
          new MortarTargetCode(pre),
          new MortarTargetCode(post),
          funcInfo.returnType.type_stackAsValue(code), jumpValues, jumpValues);
    }
  }

  @Override
  public JavaBytecode typestate_castTo(EvaluationContext context, Location location, MortarDataType prototype, MortarDeferredCode code) {
    throw new Assertion();
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    return false;
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
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
