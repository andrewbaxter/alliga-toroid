package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMBaseClassType.getArgTuple;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

public class JVMExternConstructor implements SimpleValue {
  public final JVMExternClassType base;

  public JVMExternConstructor(JVMExternClassType base) {
    this.base = base;
  }

  public static JVMExternConstructor graphDeserialize(Record data) {
    JVMExternConstructor out =
        new JVMExternConstructor((JVMExternClassType) data.data.getOpt("base"));
    return out;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    resolveSigs(context.moduleContext);
    ROTuple argTuple = getArgTuple(argument);
    JVMShallowMethodFieldType.MethodSpecDetails real = base.constructorSigs.getOpt(argTuple);
    if (real == null) {
      context.moduleContext.log.errors.add(JVMError.noMethodField(location, "<init>"));
      return EvaluateResult.error;
    }
    JVMRWSharedCode code =
        new JVMCode()
            .line(context.moduleContext.sourceLocation(location))
            .add(new TypeInsnNode(NEW, base.jvmName))
            .add(DUP);
    JVMTargetModuleContext.convertFunctionArgument(code, argument);
    code.add(new MethodInsnNode(INVOKESPECIAL, base.jvmName, "<init>", real.jvmSigDesc, false));
    return new EvaluateResult(code, null, NullValue.value);
  }

  public void resolveSigs(DirectModule module) {
    base.resolveMethods(module);
  }

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>(s -> s.putNew("base", base)));
  }
}
