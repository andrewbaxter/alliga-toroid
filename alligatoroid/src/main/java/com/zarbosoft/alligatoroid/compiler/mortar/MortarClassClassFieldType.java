package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWCode;
import com.zarbosoft.rendaw.common.Assertion;
import org.objectweb.asm.tree.FieldInsnNode;

import static org.objectweb.asm.Opcodes.GETFIELD;

public class MortarClassClassFieldType implements MortarHalfType {
  public final MortarClass type;
  public final MortarClass fieldType;
  private final String fieldName;

  public MortarClassClassFieldType(MortarClass type, MortarClass fieldType, String fieldName) {
    this.type = type;
    this.fieldType = fieldType;
    this.fieldName = fieldName;
  }

  @Override
  public Value asValue(MortarProtocode lower) {
    return fieldType.asValue(
        new MortarProtocode() {
          @Override
          public JVMCode lower() {
            return new MortarCode()
                .add(lower.lower())
                .add(
                    new FieldInsnNode(
                        GETFIELD,
                        type.jbcInternalClass,
                        fieldName,
                        JVMDescriptor.objExternal(fieldType.jbcInternalClass)));
          }

          @Override
          public TargetCode drop(Context context, Location location) {
            return null;
          }
        });
  }

    @Override
    public Value stackAsValue(JVMRWCode code) {
      throw new Assertion();
    }
}
