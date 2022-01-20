package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfValue;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.POP;

public class JVMHalfObjectType implements JVMHalfDataType {
  public static JVMHalfObjectType type = new JVMHalfObjectType();

  protected JVMHalfObjectType() {}

  @Override
  public JVMValue asValue(JVMProtocode lower) {
    return new JVMHalfValue(this, lower);
  }

  @Override
  public JVMValue stackAsValue(JVMSharedCodeElement code) {
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

  @Override
  public int storeOpcode() {
    return ASTORE;
  }

  @Override
  public int loadOpcode() {
    return ALOAD;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.OBJECT;
  }
}
