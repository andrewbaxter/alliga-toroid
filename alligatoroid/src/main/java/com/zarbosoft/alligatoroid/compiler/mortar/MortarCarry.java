package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

import java.util.function.Function;

public interface MortarCarry {
  public static MortarCarry ofHalf(JavaBytecode half, JavaBytecode drop) {
    return new MortarCarry() {
      @Override
      public JavaBytecode half(EvaluationContext context) {
        return half;
      }

      @Override
      public JavaBytecode drop(EvaluationContext context, Location location) {
        return drop;
      }
    };
  }

  public static MortarCarry ofDeferredHalf(Function<EvaluationContext, JavaBytecode> half) {
    return new MortarCarry() {
      @Override
      public JavaBytecode half(EvaluationContext context) {
        return half.apply(context);
      }

      @Override
      public JavaBytecode drop(EvaluationContext context, Location location) {
        return null;
      }
    };
  }

  public JavaBytecode half(EvaluationContext context);

  JavaBytecode drop(EvaluationContext context, Location location);
}
