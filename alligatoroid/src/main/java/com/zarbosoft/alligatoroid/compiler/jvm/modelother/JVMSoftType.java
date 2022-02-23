package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

public interface JVMSoftType extends Exportable {
  public static final Class<? extends ModuleId>[] SERIAL_UNION =
      new Class[] {
        JVMSoftTypeArray.class, JVMSoftTypeDeferred.class, JVMSoftTypeType.class,
      };

  JVMType resolve(EvaluationContext context);
}
