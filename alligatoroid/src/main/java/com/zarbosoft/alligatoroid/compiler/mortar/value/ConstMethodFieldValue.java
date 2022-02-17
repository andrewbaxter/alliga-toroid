package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.ContinueError;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStaticMethodType;

import java.lang.reflect.InvocationTargetException;

public class ConstMethodFieldValue implements Value, NoExportValue {
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
    final MortarStaticMethodType.ConvertImmediateArgRootRes res =
        MortarStaticMethodType.convertImmediateRootArg(context, type.funcInfo, argument);
    if (res.isError) return EvaluateResult.error;
    if (!res.isWhole) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return EvaluateResult.error;
    }
    try {
      return EvaluateResult.pure(
          type.funcInfo.returnType.constAsValue(type.funcInfo.method.invoke(base, res.args)));
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      final Throwable e2 = e.getTargetException();
      if (e2.getClass() != ContinueError.class) {
        context.moduleContext.errors.add(new Unexpected(location, e2));
      }
      return EvaluateResult.error;
    }
  }
}
