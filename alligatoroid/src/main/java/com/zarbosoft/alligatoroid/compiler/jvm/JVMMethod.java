package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.RETURN;

public class JVMMethod implements SimpleValue {
  private final JVMClassType base;
  private final ROTuple key;
  private final JVMShallowMethodFieldType.MethodSpecDetails specDetails;
  private JVMCode built;

  public JVMMethod(
      JVMClassType base,
      ROTuple keyTuple,
      JVMShallowMethodFieldType.MethodSpecDetails specDetails) {
    this.base = base;
    this.key = keyTuple;
    this.specDetails = specDetails;
  }

  @Builtin.WrapExpose
  public void implement(Module module, Value body) {
    String name = (String) key.get(0);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = base;
    base.methodFields.put(key, field);
    base.fields.add(name);
    JVMTargetModuleContext targetContext = new JVMTargetModuleContext();
    Context context = new Context(module, targetContext, new Scope(null));

    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    ectx.record(body.evaluate(context));
    built = (JVMCode) ectx.build(null).preEffect;
    if (module.log.errors.some()) {
      throw new MultiError(module.log.errors);
    }
    base.jvmClass.defineFunction(
        name, specDetails.jvmSigDesc, new JVMCode().add(built).add(RETURN), new TSList<>());
    base.incompleteMethods.remove(key);
  }
}
