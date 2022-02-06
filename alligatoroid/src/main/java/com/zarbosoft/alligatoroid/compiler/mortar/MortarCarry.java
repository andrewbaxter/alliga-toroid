package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

import java.util.function.Function;

public interface MortarCarry {
  public static MortarCarry ofHalf(JVMSharedCodeElement half, JVMSharedCodeElement drop) {
    return new MortarCarry() {
      @Override
      public JVMSharedCodeElement half(EvaluationContext context) {
        return half;
      }

      @Override
      public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
        return drop;
      }
    };
  }

  public static MortarCarry ofDeferredHalf(Function<EvaluationContext, JVMSharedCodeElement> half) {
    return new MortarCarry() {
      @Override
      public JVMSharedCodeElement half(EvaluationContext context) {
        return half.apply(context);
      }

      @Override
      public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
        return null;
      }
    };
  }

  public JVMSharedCodeElement half(EvaluationContext context);

  JVMSharedCodeElement drop(EvaluationContext context, Location location);
}
