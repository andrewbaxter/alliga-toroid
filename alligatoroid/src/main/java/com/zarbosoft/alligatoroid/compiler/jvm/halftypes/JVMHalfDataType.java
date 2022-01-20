package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMHalfBinding;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfValue;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.ROPair;

import static org.objectweb.asm.Opcodes.POP;

public interface JVMHalfDataType extends JVMHalfType {
  default EvaluateResult valueAccess(
      EvaluationContext context, Location location, MortarValue field, JVMProtocode lower) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default ROPair<TargetCode, JVMHalfBinding> valueBind(JVMSharedCodeElement code) {
    Object key = new Object();
    return new ROPair<>(
        new JVMSharedCode().add(code).addVarInsn(storeOpcode(), key),
        new JVMHalfBinding(key, this));
  }

  default JVMValue asValue(JVMProtocode lower) {
    return new JVMHalfValue(this, lower);
  }

  default JVMValue stackAsValue(JVMSharedCodeElement code) {
    return new JVMHalfValue(
        this,
        new JVMProtocode() {
          @Override
          public JVMSharedCodeElement jvmLower(EvaluationContext context) {
            return code;
          }

          @Override
          public JVMSharedCodeElement jvmDrop(EvaluationContext context, Location location) {
            return JVMSharedCode.inst(POP);
          }
        });
  }

  int storeOpcode();

  int loadOpcode();

  JVMSharedDataDescriptor jvmDesc();
}
