package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.rendaw.common.Assertion;

public class NullValue
    implements MortarProtocode, SimpleValue, AutoBuiltinExportable, LeafExportable {
  public static final NullValue value = new NullValue();

  private NullValue() {}

  @Override
  public JVMSharedCode mortarHalfLower(EvaluationContext context) {
    throw new Assertion();
  }

  @Override
  public JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location) {
    return null;
  }
}
