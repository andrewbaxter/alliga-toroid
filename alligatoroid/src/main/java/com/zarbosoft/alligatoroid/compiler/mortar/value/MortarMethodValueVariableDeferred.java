package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;

public class MortarMethodValueVariableDeferred implements Value {
  public final StaticAutogen.FuncInfo funcInfo;
  public final MortarDeferredCode code;

  public MortarMethodValueVariableDeferred(
      StaticAutogen.FuncInfo funcInfo, MortarDeferredCode code) {
    this.funcInfo = funcInfo;
    this.code = code;
  }

  public static EvaluateResult call(
      EvaluationContext context,
      Location location,
      JavaBytecode baseCode,
      StaticAutogen.FuncInfo funcInfo,
      Value argument) {
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    code.add(baseCode);
    if (!convertFunctionArgumentRoot(context, location, code, argument)) {
      return EvaluateResult.error;
    }
    final MortarDataType retDataType =
        funcInfo.returnType instanceof MortarDataType ? (MortarDataType) funcInfo.returnType : null;
    code.add(
        JavaBytecodeUtils.callMethod(
            context.sourceLocation(location),
            funcInfo.base.asInternalName(),
            funcInfo.name,
            JavaMethodDescriptor.fromParts(
                retDataType == null ? Global.DESC_VOID : retDataType.type_jvmDesc(),
                funcInfo.argDescriptor())));
    if (retDataType == null) {
      return EvaluateResult.simple(Global.NULL_VALUE, new MortarTargetCode(code));
    } else {
      return EvaluateResult.simple(retDataType.type_stackAsValue(), new MortarTargetCode(code));
    }
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    return call(context, location, code.consume(), funcInfo, argument);
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    context.errors.add(new GeneralLocationError(id, "Methods can't be the results of branches"));
    return EvaluateResult.error;
  }

  @Override
  public AlligatorusType type(EvaluationContext context) {
    throw new Assertion();
  }
}
