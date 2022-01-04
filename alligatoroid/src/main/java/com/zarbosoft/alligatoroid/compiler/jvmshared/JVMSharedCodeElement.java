package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.TSList;
import org.objectweb.asm.MethodVisitor;

public interface JVMSharedCodeElement extends TargetCode {
  String JVM_TARGET_NAME = "jvm";

  void dispatch(Dispatcher dispatcher);

  @Override
  default String targetName() {
    return JVM_TARGET_NAME;
  }

  interface Dispatcher {
    void handleNested(JVMSharedCode code);

    void handleStoreLoad(JVMSharedCodeStoreLoad storeLoad);

    void handleInstruction(JVMSharedCodeInstruction instruction);
  }
}
