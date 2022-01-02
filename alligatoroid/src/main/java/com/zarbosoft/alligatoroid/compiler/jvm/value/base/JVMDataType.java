package com.zarbosoft.alligatoroid.compiler.jvm.value.base;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMBinding;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.half.JVMValue;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

import static org.objectweb.asm.Opcodes.POP;

public interface JVMDataType extends JVMType, AutoGraphMixin {
  default EvaluateResult valueAccess(
      EvaluationContext context, Location location, Value field, JVMProtocode lower) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  default Value graphDeserializeValue(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    throw new Assertion();
  }

  default ROPair<TargetCode, Binding> valueBind(EvaluationContext module, JVMProtocode lower) {
    Object key = new Object();
    return new ROPair<>(
        new JVMCode().add(lower.lower(module)).addVarInsn(storeOpcode(), key),
        new JVMBinding(key, this));
  }

  default Value asValue(JVMProtocode lower) {
    return new JVMValue(this, lower);
  }

  default Value stackAsValue(JVMCode code) {
    return new JVMValue(
        this,
        new JVMProtocode() {
          @Override
          public JVMCode lower(EvaluationContext context) {
            return code;
          }

          @Override
          public TargetCode drop(EvaluationContext context, Location location) {
            return new JVMCode().add(POP);
          }
        });
  }

  int storeOpcode();

  int loadOpcode();

  String jvmDesc();
}
