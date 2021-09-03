package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ModuleContext;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.RETURN;

public class JVMMethod implements SimpleValue {
  private final JVMClassType base;
  private final String name;
  private String externName;
  private JVMCode built;

  public JVMMethod(JVMClassType base, String name) {
    this.base = base;
    this.name = name;
    this.externName = name;
  }

  public void setJvmName(String name) {
    this.externName = name;
  }

  public void implement(Record spec, Value body) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.specDetails(spec);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, externName, specDetails.jvmSigDesc);
    field.base = base;
    base.fields.putNew(name, field);
    ModuleContext moduleContext = new ModuleContext(null, null);
    JVMTargetModuleContext targetContext = new JVMTargetModuleContext();
    Context context = new Context(moduleContext, targetContext, new Scope(null));

    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    ectx.record(body.evaluate(context));
    built = (JVMCode) ectx.build(null).preEffect;
    if (moduleContext.log.errors.some()) {
      throw new MultiError(moduleContext.log.errors);
    }
    base.jvmClass.defineFunction(
        externName, specDetails.jvmSigDesc, new JVMCode().add(built).add(RETURN), new TSList<>());
    base.incompleteMethods.remove(name);
  }
}
