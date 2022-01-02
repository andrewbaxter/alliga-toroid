package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarUnlowerer;
import com.zarbosoft.alligatoroid.compiler.mortar.value.half.MortarHalfValue;
import com.zarbosoft.rendaw.common.ROPair;

import static org.objectweb.asm.Opcodes.POP;

public interface MortarHalfDataType extends MortarHalfType, MortarUnlowerer {
  default Value asValue(MortarProtocode lower) {
    return new MortarHalfValue(this, lower);
  }

  default Value stackAsValue(MortarCode code) {
    return new MortarHalfValue(
        this,
        new MortarProtocode() {
          @Override
          public MortarCode lower() {
            return code;
          }

          @Override
          public TargetCode drop(EvaluationContext context, Location location) {
            return new MortarCode().addI(POP);
          }
        });
  }

  int storeOpcode();

  int loadOpcode();

  default ROPair<TargetCode, Binding> valueBind(MortarProtocode lower) {
    Object key = new Object();
    return new ROPair<>(
        new MortarCode().add(lower.lower()).addVarInsn(storeOpcode(), key),
        new MortarHalfBinding(key, this));
  }

  int returnOpcode();

  String jvmDesc();

  MortarTargetModuleContext.LowerResult box(JVMSharedCodeElement valueCode);
}
