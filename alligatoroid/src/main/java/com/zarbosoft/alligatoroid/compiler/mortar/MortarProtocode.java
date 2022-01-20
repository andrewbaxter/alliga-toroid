package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface MortarProtocode {
  public JVMSharedCodeElement mortarHalfLower(EvaluationContext context);

  JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location);
}
