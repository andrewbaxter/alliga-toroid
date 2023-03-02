package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue.nullValue;

public class MethodValue implements Value {
  public final Meta.FuncInfo funcInfo;
  public final MortarDeferredCode code;

  public MethodValue(Meta.FuncInfo funcInfo, MortarDeferredCode code) {
    this.funcInfo = funcInfo;
    this.code = code;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    JavaBytecodeSequence pre = new JavaBytecodeSequence();
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    JavaBytecodeSequence post = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, pre, code, post, argument)) {
      return EvaluateResult.error;
    }
    code.add(this.code.consume());
    code.add(
        JavaBytecodeUtils.callStaticMethod(
            context.sourceLocation(location),
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
          funcInfo.returnType.protoTypeStackAsValue(code));
  }
}
