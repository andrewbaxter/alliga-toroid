package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarUnlowerer;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarHalfValue;
import com.zarbosoft.rendaw.common.ROPair;

import static org.objectweb.asm.Opcodes.POP;

public interface MortarHalfDataType extends MortarHalfType, MortarUnlowerer {
  default MortarValue asValue(Location location, MortarProtocode lower) {
    return new MortarHalfValue(this, lower);
  }

  default MortarValue stackAsValue(JVMSharedCode code) {
    return new MortarHalfValue(
        this,
        new MortarProtocode() {
          @Override
          public JVMSharedCodeElement mortarHalfLower(EvaluationContext context) {
            return code;
          }

          @Override
          public JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location) {
            return JVMSharedCode.inst(POP);
          }
        });
  }

  int storeOpcode();

  int loadOpcode();

  default ROPair<TargetCode, MortarBinding> valueBind(EvaluationContext context, MortarProtocode lower) {
    Object key = new Object();
    return new ROPair<>(
        new JVMSharedCode().add(lower.mortarHalfLower(context)).addVarInsn(storeOpcode(), key),
        new MortarHalfBinding(key, this));
  }

  int returnOpcode();

  JVMSharedDataDescriptor jvmDesc();

  MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode);
}
