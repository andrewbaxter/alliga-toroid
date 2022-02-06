package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.Assertion;

public class JVMTargetModuleContext implements TargetModuleContext {
  public static JVMSharedCodeElement lowerValue(VariableDataStackValue value) {
    if (value instanceof LooseTuple) {
      throw new Assertion(); // Loose tuple only allowed for first level of function call, otherwise
      // needs to be proper jvm type (tuples don't exist in jvm) - should be
      // checked elsewhere
    } else if (value instanceof ConstString) {
      return JVMSharedCode.string(((ConstString) value).value);
    } else {
      // TODO transfer
      throw new Assertion();
    }
  }

  public static void convertFunctionArgument(
      EvaluationContext context, JVMSharedCode code, VariableDataStackValue argument) {
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        if (e.preEffect != null) code.add((JVMSharedCode) e.preEffect);
        code.add(lower(context, e.value));
      }
    } else {
      code.add(lower(context, argument));
    }
  }

  public static JVMSharedCodeElement lower(EvaluationContext context, Value value) {
    if (value instanceof ConstString) {
      return JVMSharedCode.string(((ConstString) value).value);
    } else if (value instanceof JVMHalfValue) {
      return ((JVMHalfValue) value).jvmLower(context);
    } else {
      throw new Assertion();
    }
  }

  @Override
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> chunks) {
    JVMSharedCode code = new JVMSharedCode();
    for (TargetCode chunk : chunks) {
      if (chunk == null) continue;
      if (!(chunk instanceof JVMSharedCode)) {
        throw new Assertion();
      }
      code.add((JVMSharedCode) chunk);
    }
    return code;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location, ConstPrimitive child) {
    return child.dispatch(
        new ConstPrimitive.Dispatcher<EvaluateResult>() {
          @Override
          public EvaluateResult handleString(ConstString value) {
            return EvaluateResult.pure(
                JVMHalfStringType.type.asValue(
                    JVMProtocode.ofDeferred(c -> JVMSharedCode.string(value.value))));
          }

          @Override
          public EvaluateResult handleBool(ConstBool value) {
            return EvaluateResult.pure(
                JVMHalfBoolType.type.asValue(
                    JVMProtocode.ofDeferred(c -> JVMSharedCode.bool_(value.value))));
          }

          @Override
          public EvaluateResult handleInt(ConstInt value) {
            return EvaluateResult.pure(
                JVMHalfIntType.type.asValue(
                    JVMProtocode.ofDeferred(ctx -> JVMSharedCode.int_(value.value))));
          }
        });
  }
}
