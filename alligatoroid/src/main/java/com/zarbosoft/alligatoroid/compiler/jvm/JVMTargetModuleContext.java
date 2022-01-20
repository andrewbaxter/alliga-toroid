package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfValue;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.error.IncompatibleTargetValues;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeString;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement.JVM_TARGET_NAME;

public class JVMTargetModuleContext implements TargetModuleContext {
  public static JVMSharedCodeElement lowerValue(MortarValue value) {
    if (value instanceof LooseTuple) {
      throw new Assertion(); // Loose tuple only allowed for first level of function call, otherwise
      // needs to be proper jvm type (tuples don't exist in jvm) - should be
      // checked elsewhere
    } else if (value instanceof WholeString) {
      return JVMSharedCode.string(((WholeString) value).value);
    } else {
      // TODO transfer
      throw new Assertion();
    }
  }

  public static void convertFunctionArgument(
      EvaluationContext context, JVMSharedCode code, MortarValue argument) {
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
    if (value instanceof WholeString) {
      return JVMSharedCode.string(((WholeString) value).value);
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
        context.moduleContext.errors.add(
            new IncompatibleTargetValues(location, JVM_TARGET_NAME, chunk.targetName()));
        return null;
      }
      code.add((JVMSharedCode) chunk);
    }
    return code;
  }

  @Override
  public ROPair<TargetCode, ? extends Binding> bind(
      EvaluationContext context, Location location, Value value) {
    if (value == ErrorValue.error) return new ROPair<>(null, ErrorBinding.binding);
    return ((JVMValue) value).jvmBind(context, location);
  }

  @Override
  public EvaluateResult call(
      EvaluationContext context, Location location, Value target, Value args) {
    if (target == ErrorValue.error || args == ErrorValue.error) return EvaluateResult.error;
    return ((JVMValue) target).jvmCall(context, location, (MortarValue) args);
  }

  @Override
  public EvaluateResult access(
      EvaluationContext context, Location location, Value target, Value field) {
    if (target == ErrorValue.error || field == ErrorValue.error) return EvaluateResult.error;
    return ((JVMValue) target).jvmAccess(context, location, (MortarValue) field);
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location, Binding binding) {
    if (binding == ErrorBinding.binding) return EvaluateResult.error;
    return ((JVMHalfBinding) binding).jvmFork(context, location);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location, Value value) {
    if (value == ErrorValue.error) return null;
    return ((JVMValue) value).jvmDrop(context, location);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location, Binding binding) {
    if (binding == ErrorBinding.binding) return null;
    return ((JVMHalfBinding) binding).jvmDrop(context, location);
  }
}
