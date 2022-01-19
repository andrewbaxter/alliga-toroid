package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;

public class MortarHalfFieldType implements MortarHalfType {
  private final MortarHalfDataType dataType;
  private final JVMSharedJVMName jvmName;
  private final String fieldName;

  public MortarHalfFieldType(
      MortarHalfDataType dataType, JVMSharedJVMName jvmName, String fieldName) {
    this.dataType = dataType;
    this.jvmName = jvmName;
    this.fieldName = fieldName;
  }

  @Override
  public Value asValue(Location location, MortarProtocode lower) {
    return dataType.asValue(
        location,
        new MortarProtocode() {
          @Override
          public JVMSharedCodeElement lower(EvaluationContext context) {
            return new JVMSharedCode()
                .add(lower.lower(context))
                .add(
                    JVMSharedCode.accessField(
                        context.sourceLocation(location), jvmName, fieldName, dataType.jvmDesc()));
          }

          @Override
          public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
            return null;
          }
        });
  }
}
