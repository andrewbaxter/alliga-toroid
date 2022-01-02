package com.zarbosoft.alligatoroid.compiler.jvm.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import com.zarbosoft.rendaw.common.ROTuple;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType.getArgTuple;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

public class JVMConstructor implements SimpleValue, AutoGraphMixin, LeafValue {
  public JVMClassType base;

  public JVMConstructor(JVMClassType base) {
    this.base = base;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    resolveSigs(context);
    ROTuple argTuple = getArgTuple(argument);
    JVMUtils.MethodSpecDetails real = base.constructors.getOpt(argTuple);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noMethodField(location, "<init>"));
      return EvaluateResult.error;
    }
    JVMRWSharedCode code =
        new JVMCode()
            .line(context.sourceLocation(location))
            .add(new TypeInsnNode(NEW, base.jvmName))
            .add(DUP);
    JVMTargetModuleContext.convertFunctionArgument(code, argument);
    code.add(new MethodInsnNode(INVOKESPECIAL, base.jvmName, "<init>", real.jvmSigDesc, false));
    return new EvaluateResult(code, null, NullValue.value);
  }

  public void resolveSigs(EvaluationContext context) {
    base.resolveMethods(context);
  }
}
