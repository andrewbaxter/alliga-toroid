package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

public class JVMClassType extends JVMBaseClassType {
  public final TSSet<ROTuple> incompleteMethods = new TSSet<>();
  public final JVMSharedClass jvmClass;
  public byte[] built;

  public JVMClassType(String jvmExternalClass) {
    super(
        jvmExternalClass,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    this.jvmClass = new JVMSharedClass(jvmExternalClass);
  }

  public void build(TSList<Error> errors) {
    if (built != null) return;
    if (incompleteMethods.some()) {
      errors.add(Error.methodsNotDefined(incompleteMethods));
      return;
    }
    built = jvmClass.render();
  }
}
