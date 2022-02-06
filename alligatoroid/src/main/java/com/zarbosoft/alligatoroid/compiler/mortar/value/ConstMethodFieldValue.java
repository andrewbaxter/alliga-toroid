package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;

import java.lang.reflect.InvocationTargetException;

public class ConstMethodFieldValue implements Value {
  private final Object base;
  private final MortarMethodFieldType type;

  public ConstMethodFieldValue(MortarMethodFieldType type, Object base) {
    this.type = type;
    this.base = base;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    final Object[] args =
        StaticMethodValue.convertImmediateRootArg(context, type.funcInfo, base, argument);
    if (args == null) {
      context.moduleContext.errors.add(new ValueNotWhole(location, argument));
      return EvaluateResult.error;
    }
    try {
      return EvaluateResult.pure(
          type.funcInfo.returnType.constAsValue(type.funcInfo.method.invoke(args)));
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      context.moduleContext.errors.add(new Unexpected(location, e.getTargetException()));
      return EvaluateResult.error;
    }
  }
}
