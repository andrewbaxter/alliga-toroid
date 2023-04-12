package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;

public class MortarFunctionValueVariableField implements Value {
  public final StaticAutogen.FuncInfo funcInfo;
  public final MortarDeferredCode code;

  public MortarFunctionValueVariableField(
      StaticAutogen.FuncInfo funcInfo, MortarDeferredCode code) {
    this.funcInfo = funcInfo;
    this.code = code;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    JavaBytecodeSequence pre = new JavaBytecodeSequence();
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (!convertFunctionArgumentRoot(context, location, pre, code, argument)) {
      return EvaluateResult.error;
    }
    code.add(this.code.consume());
    code.add(
        JavaBytecodeUtils.callStaticMethod(
            context.sourceLocation(location),
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
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }

  @Override
  public boolean canCastTo(AlligatorusType type) {
  return false;
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
  throw new Assertion();
  }

  @Override
  public EvaluateResult unfork(EvaluationContext context, Location location, TSList<Value> otherValues) {
    TODO();
  }
}
