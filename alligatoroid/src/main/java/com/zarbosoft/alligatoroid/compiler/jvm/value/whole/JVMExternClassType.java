package com.zarbosoft.alligatoroid.compiler.jvm.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.value.direct.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.AutoBuiltinClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class JVMExternClassType extends JVMClassType {
  private Value setup;
  boolean finished;
  private boolean setupDone = false;

  public JVMExternClassType(JVMSharedNormalName name, Value setup) {
    super(
        name,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    finished = false;
    this.setup = setup;
  }

  @Override
  public void resolveMethods(EvaluationContext context) {
    if (setupDone) return;
    AutoBuiltinClassType classValueType = Meta.wrappedClasses.get(JVMExternClassBuilder.class);
    Evaluator.evaluate(
        context.moduleContext,
        new TSList<>(setup),
        new TSOrderedMap<WholeValue, Value>()
            .put(
                new WholeString("class"), classValueType.unlower(new JVMExternClassBuilder(this))));
    setupDone = true;
  }
}
