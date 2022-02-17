package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class JVMExternClassInstanceType extends JVMClassInstanceType {
  @Param public LanguageElement setup;
  @Param private boolean setupDone = false;
  @Param private boolean setupError = false;

  public static JVMExternClassInstanceType blank(JVMSharedNormalName name, LanguageElement setup) {
    final JVMExternClassInstanceType out = new JVMExternClassInstanceType();
    out.name = name;
    out.setup = setup;
    out.constructors = new TSMap<>();
    out.fields = new TSMap<>();
    out.staticFields = new TSMap<>();
    out.inherits = new TSList<>();
    out.postInit();
    return out;
  }

  @Override
  public boolean resolveInternals(EvaluationContext context) {
    if (setupError) return false;
    if (setupDone) return true;
    MortarDataType builderType = Meta.autoMortarHalfDataTypes.get(JVMExternClassBuilder.class);
    final SemiserialModule res =
        Evaluator.evaluate(
            context.moduleContext,
            new TSList<>(setup),
            new TSOrderedMap<Object, Value>()
                .put("class", Meta.autoMortarHalfDataTypes.get(getClass()).constAsValue(this))
                .put("builder", builderType.constAsValue(new JVMExternClassBuilder(this))));
    if (res == null) {
      setupError = true;
      return false;
    }
    setupDone = true;
    return true;
  }
}
