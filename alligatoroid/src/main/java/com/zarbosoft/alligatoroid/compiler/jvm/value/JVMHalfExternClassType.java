package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class JVMHalfExternClassType extends JVMHalfClassType {
  public LanguageElement setup;
  private boolean setupDone = false;
  private boolean setupError = false;

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

  public static JVMHalfExternClassType blank(JVMSharedNormalName name, LanguageElement setup) {
    final JVMHalfExternClassType out = new JVMHalfExternClassType(name, setup);
    out.postInit();
    return out;
  }

  @Override
  public boolean resolveInternals(EvaluationContext context) {
    if (setupError) return false;
    if (setupDone) return true;
    MortarAutoObjectType builderType =
        Meta.autoMortarHalfDataTypes.get(JVMExternClassBuilder.class);
    final SemiserialModule res =
        Evaluator.evaluate(
            context.moduleContext,
            new TSList<>(setup),
            new TSOrderedMap<Object, VariableDataStackValue>()
                .put("class", this)
                .put(
                    "builder",
                    builderType.constAsValue(new JVMExternClassBuilder(this))));
    if (res == null) {
      setupError = true;
      return false;
    }
    setupDone = true;
    return true;
  }
}
