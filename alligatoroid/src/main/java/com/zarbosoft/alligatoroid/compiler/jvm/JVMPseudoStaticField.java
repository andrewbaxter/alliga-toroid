package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoGraphBuiltinValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROTuple;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMBaseClassType.getArgTuple;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class JVMPseudoStaticField implements SimpleValue, AutoGraphBuiltinValue {
  public final String name;
  public JVMBaseClassType base;

  public JVMPseudoStaticField(JVMBaseClassType base, String name) {
    this.base = base;
    this.name = name;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    ROTuple argTuple = getArgTuple(argument);
    JVMShallowMethodFieldType real =
        base.methodFields.getOpt(ROTuple.create(name).append(argTuple));
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noMethodField(location, name));
      return EvaluateResult.error;
    }
    JVMRWSharedCode code = new JVMCode();
    JVMTargetModuleContext.convertFunctionArgument(code, argument);
    code.line(context.sourceLocation(location))
        .add(new MethodInsnNode(INVOKESTATIC, real.base.jvmName, real.name, real.jvmDesc, false));
    if (real.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.returnType.stackAsValue((JVMCode) code));
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    JVMDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return EvaluateResult.error;
    }
    return real.valueAccess(
        context,
        location,
        field,
        new JVMProtocode() {
          @Override
          public TargetCode drop(EvaluationContext context, Location location) {
            return null;
          }

          @Override
          public JVMSharedCode lower(EvaluationContext context) {
            return new JVMCode()
                .add(new FieldInsnNode(GETSTATIC, base.jvmName, name, real.jvmDesc()));
          }
        });
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    JVMDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return new ROPair<>(null, ErrorBinding.binding);
    }
    return real.valueBind(
        context,
        new JVMProtocode() {
          @Override
          public TargetCode drop(EvaluationContext context, Location location) {
            return null;
          }

          @Override
          public JVMSharedCode lower(EvaluationContext context) {
            return new JVMCode()
                .add(new FieldInsnNode(GETSTATIC, base.jvmName, name, real.jvmDesc()));
          }
        });
  }

  @Override
  public Value graphDeserializeValue(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    throw new Assertion();
  }
}
