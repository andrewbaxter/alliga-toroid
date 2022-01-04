package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;

public interface MortarProtocode {
  public JVMSharedCodeElement lower(EvaluationContext context);

  JVMSharedCodeElement drop(EvaluationContext context, Location location);
}
