package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Binding;
import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROTuple;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMBaseClassType.getArgTuple;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class JVMPseudoStaticField implements SimpleValue {
  public final JVMBaseClassType base;
  public final String name;

  public JVMPseudoStaticField(JVMBaseClassType base, String name) {
    this.base = base;
    this.name = name;
  }

  @Override
  public EvaluateResult call(Context context, Location location, Value argument) {
    ROTuple argTuple = getArgTuple(argument);
    JVMShallowMethodFieldType real =
        base.methodFields.getOpt(ROTuple.create(name).append(argTuple));
    if (real == null) {
      context.module.log.errors.add(JVMError.noMethodField(location, name, argTuple));
      return EvaluateResult.error;
    }
    JVMRWSharedCode code = new JVMCode();
    JVMTargetModuleContext.convertFunctionArgument(code, argument);
    code.line(context.module.sourceLocation(location))
        .add(
            new MethodInsnNode(
                INVOKESTATIC, real.base.jvmName, real.name, real.jvmDesc, false));
    if (real.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.returnType.stackAsValue((JVMCode) code));
  }

  @Override
  public EvaluateResult access(Context context, Location location, Value field) {
    JVMDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.module.log.errors.add(JVMError.noDataField(location, name));
      return EvaluateResult.error;
    }
    return real.valueAccess(
        context,
        location,
        field,
        new JVMProtocode() {
          @Override
          public TargetCode drop(Context context, Location location) {
            return null;
          }

          @Override
          public JVMSharedCode lower(Module module) {
            return new JVMCode()
                .add(
                    new FieldInsnNode(
                        GETSTATIC, base.jvmName, name, real.jvmDesc()));
          }
        });
  }

  @Override
  public ROPair<TargetCode, Binding> bind(Context context, Location location) {
    JVMDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.module.log.errors.add(JVMError.noDataField(location, name));
      return new ROPair<>(null, ErrorBinding.binding);
    }
    return real.valueBind(
        context.module,
        new JVMProtocode() {
          @Override
          public TargetCode drop(Context context, Location location) {
            return null;
          }

          @Override
          public JVMSharedCode lower(Module module) {
            return new JVMCode()
                .add(
                    new FieldInsnNode(
                        GETSTATIC, base.jvmName, name, real.jvmDesc()));
          }
        });
  }
}
