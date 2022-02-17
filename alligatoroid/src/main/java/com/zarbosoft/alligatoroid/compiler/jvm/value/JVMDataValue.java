package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;

public interface JVMDataValue extends Value, NoExportValue {
  /**
   * @param context
   * @param location
   * @return null if error
   */
  JVMProtocode jvmCode(EvaluationContext context, Location location);

  JVMType jvmType();
}
