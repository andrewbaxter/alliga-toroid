package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.rendaw.common.TSList;
import org.jetbrains.annotations.NotNull;

public class MortarStringType extends MortarBaseObjectType implements MortarPrimitiveType {
  public static final MortarStringType type = new MortarStringType();

  private MortarStringType() {}

  @Override
  public EvaluateResult type_valueVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(type_stackAsValue(JavaBytecodeUtils.literalString((String) data)));
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  @NotNull
  private static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.STRING;
  }

  @Override
  public JavaDataDescriptor tuple_fieldtype_jvmDesc() {
    return jvmDesc();
  }
}
