package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.rendaw.common.Assertion;

public class NullValue implements MortarProtocode, SimpleValue, LeafExportable, AutoBuiltinExportable {
  public static final NullValue value = new NullValue();

  private NullValue() {}

  @Override
  public JVMSharedCode lower(EvaluationContext context) {
    throw new Assertion();
  }

  @Override
  public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
    return null;
  }
}
