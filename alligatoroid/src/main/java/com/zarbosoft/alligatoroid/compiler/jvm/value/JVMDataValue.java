package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.rendaw.common.ROList;

public interface JVMDataValue extends Value, NoExportValue {
  /**
   * @param context
   * @param location
   * @return null if error
   */
  JVMProtocode jvmCode(EvaluationContext context, Location location);

  @Override
  default ROList<String> traceFields(EvaluationContext context) {
    return jvmType().traceFields();
  }

  JVMType jvmType();
}
