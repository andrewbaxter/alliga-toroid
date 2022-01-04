package com.zarbosoft.alligatoroid.compiler.jvm.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;

public class JVMConstructor implements SimpleValue, AutoGraphMixin, LeafValue {
  private final Record spec;
  public JVMClassType base;
  public JVMUtils.MethodSpecDetails specDetails;

  public JVMConstructor(JVMClassType base, Record spec) {
    this.base = base;
    this.spec = spec;
  }

  public static JVMConstructor create(JVMClassType base, Record spec) {
    final JVMConstructor out = new JVMConstructor(base, spec);
    out.postDesemiserialize();
    return out;
  }

  @Override
  public void postDesemiserialize() {
    specDetails = JVMUtils.methodSpecDetails(spec);
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    base.resolveMethods(context);
    JVMSharedCode code = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.instantiate(
            context.sourceLocation(location), base.name, specDetails.jvmSigDesc, code));
    return new EvaluateResult(code, null, NullValue.value);
  }
}
