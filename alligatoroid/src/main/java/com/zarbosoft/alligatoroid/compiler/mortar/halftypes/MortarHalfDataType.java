package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarUnlowerer;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarHalfValue;
import com.zarbosoft.rendaw.common.ROPair;

import static org.objectweb.asm.Opcodes.POP;

public interface MortarHalfDataType extends MortarHalfType, MortarUnlowerer {
  default Value asValue(Location location, MortarProtocode lower) {
    return new MortarHalfValue(this, lower);
  }

  default Value stackAsValue(JVMSharedCode code) {
    return new MortarHalfValue(
        this,
        new MortarProtocode() {
          @Override
          public JVMSharedCodeElement lower(EvaluationContext context) {
            return code;
          }

          @Override
          public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
            return JVMSharedCode.inst(POP);
          }
        });
  }

  int storeOpcode();

  int loadOpcode();

  default ROPair<TargetCode, Binding> valueBind(EvaluationContext context, MortarProtocode lower) {
    Object key = new Object();
    return new ROPair<>(
        new JVMSharedCode().add(lower.lower(context)).addVarInsn(storeOpcode(), key),
        new MortarHalfBinding(key, this));
  }

  int returnOpcode();

  JVMSharedDataDescriptor jvmDesc();

  MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode);
}
