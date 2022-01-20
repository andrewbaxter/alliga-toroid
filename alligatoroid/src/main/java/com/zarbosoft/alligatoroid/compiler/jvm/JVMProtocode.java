package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface JVMProtocode {
  JVMSharedCodeElement jvmDrop(EvaluationContext context, Location location);

  JVMSharedCodeElement jvmLower(EvaluationContext context);
}
