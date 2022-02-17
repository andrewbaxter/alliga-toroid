package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.TSList;
import org.objectweb.asm.MethodVisitor;

public interface JVMSharedCodeElement extends TargetCode {
  String JVM_TARGET_NAME = "jvm";

  static boolean empty(JVMSharedCodeElement e) {
  if (e == null) return true;
  if (e instanceof JVMSharedCode && ((JVMSharedCode)e).size() == 0) return true;
  return false;}

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
