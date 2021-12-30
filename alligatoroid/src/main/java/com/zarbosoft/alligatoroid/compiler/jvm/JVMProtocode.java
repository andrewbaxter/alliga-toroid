package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface JVMProtocode {
  TargetCode drop(EvaluationContext context, Location location);

  JVMSharedCode lower(EvaluationContext context);
}
