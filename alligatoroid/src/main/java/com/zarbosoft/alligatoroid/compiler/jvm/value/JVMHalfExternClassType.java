package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfAutoType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class JVMHalfExternClassType extends JVMHalfClassType {
  public LanguageElement setup;
  private boolean setupDone = false;

  public JVMHalfExternClassType(JVMSharedNormalName name, LanguageElement setup) {
    super(
        name,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    this.setup = setup;
  }

  @Override
  public void resolveMethods(EvaluationContext context) {
    if (setupDone) return;
    MortarHalfAutoType classValueType =
        Meta.autoMortarHalfDataTypes.get(JVMExternClassBuilder.class);
    Evaluator.evaluate(
        context.moduleContext,
        setup,
        new TSOrderedMap<WholeValue, Value>()
            .put(
                new WholeString("class"), classValueType.unlower(new JVMExternClassBuilder(this))));
    setupDone = true;
  }
}
