package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMRWSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMBaseClassType.getArgTuple;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

public class JVMExternConstructor implements SimpleValue, GraphSerializable {
  public final JVMBaseClassType base;
  public final TSList<Record> preSigs;
  public final TSMap<ROTuple, JVMShallowMethodFieldType.MethodSpecDetails> sigs = new TSMap<>();
  private boolean resolved;

  public JVMExternConstructor(JVMBaseClassType base, TSList<Record> preSigs) {
    this.base = base;
    this.preSigs = preSigs;
  }

  public static JVMExternConstructor graphDeserialize(Record data) {
    JVMExternConstructor out =
        new JVMExternConstructor(
            (JVMBaseClassType) data.data.getOpt("base"),
            (TSList) ((Tuple) data.data.getOpt("sigs")).data.mut());
    return out;
  }

  @Override
  public EvaluateResult call(Context context, Location location, Value argument) {
    resolveSigs(context.module);
    ROTuple argTuple = getArgTuple(argument);
    JVMShallowMethodFieldType.MethodSpecDetails real = sigs.getOpt(argTuple);
    if (real == null) {
      context.module.log.errors.add(JVMError.noMethodField(location, "<init>", argTuple));
      return EvaluateResult.error;
    }
    JVMRWSharedCode code =
        new JVMCode()
            .line(context.module.sourceLocation(location))
            .add(new TypeInsnNode(NEW, base.jvmName))
            .add(DUP);
    JVMTargetModuleContext.convertFunctionArgument(code, argument);
    code.add(new MethodInsnNode(INVOKESPECIAL, base.jvmName, "<init>", real.jvmSigDesc, false));
    return new EvaluateResult(code, null, NullValue.value);
  }

  private void resolveOne(Module module, Record spec) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.methodSpecDetails(module, spec);
    sigs.put(specDetails.keyTuple, specDetails);
  }

  public void resolveSigs(Module module) {
    if (resolved) return;
    for (Record spec : preSigs) {
      resolveOne(module, spec);
    }
    resolved = true;
  }

  @Override
  public Record graphSerialize() {
    return new Record(
        new TSMap<>(s -> s.putNew("base", base).putNew("sigs", new Tuple((TSList) preSigs))));
  }
}
