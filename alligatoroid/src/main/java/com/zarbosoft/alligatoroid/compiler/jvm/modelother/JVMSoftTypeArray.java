package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMArrayType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;

public class JVMSoftTypeArray implements JVMSoftType, AutoBuiltinExportable {
  @Param public JVMSoftType inner;

  public static JVMSoftTypeArray create(JVMSoftType type) {
    final JVMSoftTypeArray out = new JVMSoftTypeArray();
    out.inner = type;
    return out;
  }

  @Override
  public JVMType resolve(EvaluationContext context) {
    final JVMType inner = this.inner.resolve(context);
    if (inner == null) return null;
    return JVMArrayType.create(inner);
  }
}
