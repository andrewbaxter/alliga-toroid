package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeString;
import com.zarbosoft.rendaw.common.Assertion;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarCode.MORTAR_TARGET_NAME;

public class JVMTargetModuleContext implements TargetModuleContext {
  public static JVMSharedCode lowerValue(Value value) {
    if (value instanceof LooseTuple) {
      throw new Assertion(); // Loose tuple only allowed for first level of function call, otherwise
      // needs to be proper jvm type (tuples don't exist in jvm) - should be
      // checked elsewhere
    } else if (value instanceof WholeString) {
      return new MortarCode().addString(((WholeString) value).value);
    } else {
      // TODO transfer
      throw new Assertion();
    }
  }

  public static void convertFunctionArgument(JVMRWSharedCode code, Value argument) {
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        if (e.preEffect != null) code.add((JVMSharedCode) e.preEffect);
        code.add(lower(e.value));
      }
    } else {
      code.add(lower(argument));
    }
  }

  public static JVMSharedCode lower(Value value) {
    if (value instanceof WholeString) {
      return new MortarCode().addString(((WholeString) value).value);
    } else if (value instanceof MortarHalfValue) {
      return ((MortarHalfValue) value).lower();
    } else {
      throw new Assertion();
    }
  }

  @Override
  public TargetCode merge(Context context, Location location, Iterable<TargetCode> chunks) {
    JVMRWSharedCode code = new JVMCode();
    for (TargetCode chunk : chunks) {
      if (chunk == null) continue;
      if (!(chunk instanceof JVMCode)) {
        context.module.log.errors.add(
            Error.incompatibleTargetValues(location, MORTAR_TARGET_NAME, chunk.targetName()));
        return null;
      }
      code.add((JVMCode) chunk);
    }
    return code;
  }
}
