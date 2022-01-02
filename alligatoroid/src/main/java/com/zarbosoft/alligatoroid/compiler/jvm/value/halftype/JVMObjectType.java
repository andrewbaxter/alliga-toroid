package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.half.JVMValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.POP;

public class JVMObjectType implements JVMDataType, SimpleValue {
  public static JVMObjectType type = new JVMObjectType();

  protected JVMObjectType() {}

  @Override
  public Value asValue(JVMProtocode lower) {
    return new JVMValue(this, lower);
  }

  @Override
  public Value stackAsValue(JVMCode code) {
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

  @Override
  public int storeOpcode() {
    return ASTORE;
  }

  @Override
  public int loadOpcode() {
    return ALOAD;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.objectDescriptor;
  }
}
