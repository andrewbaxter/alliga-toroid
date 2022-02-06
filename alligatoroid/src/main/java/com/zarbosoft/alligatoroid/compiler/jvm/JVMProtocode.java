package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

import java.util.function.Function;

public interface JVMProtocode {
  public static JVMProtocode of(JVMSharedCodeElement code, JVMSharedCodeElement drop) {
    return new JVMProtocode() {
      @Override
      public JVMSharedCodeElement code(EvaluationContext context) {
        return code;
      }

      @Override
      public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
        return new JVMSharedCode().add(code).add(drop);
      }
    };
  }

  public static JVMProtocode ofDeferred(Function<EvaluationContext, JVMSharedCodeElement> half) {
    return new JVMProtocode() {
      @Override
      public JVMSharedCodeElement code(EvaluationContext context) {
        return half.apply(context);
      }

      @Override
      public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
        return null;
      }
    };
  }

  JVMSharedCodeElement drop(EvaluationContext context, Location location);

  JVMSharedCodeElement code(EvaluationContext context);
}
