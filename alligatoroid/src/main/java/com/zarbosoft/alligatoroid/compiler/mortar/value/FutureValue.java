package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.RootExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

import java.util.concurrent.Future;

public class FutureValue implements SimpleValue, LeafExportable {
  public static final RootExportable exportableType =
      new RootExportable() {
        @Override
        public SemiserialSubvalue graphSemiserializeChild(
            Exportable child,
            ImportId spec,
            Semiserializer semiserializer,
            ROList<Exportable> path,
            ROList<String> accessPath) {
          final MortarValue value = ((FutureValue) child).get();
          return value
              .type()
              .graphSemiserializeChild(value, spec, semiserializer, path, accessPath);
        }

        @Override
        public Exportable graphDesemiserializeChild(
            ModuleCompileContext context,
            Desemiserializer typeDesemiserializer,
            SemiserialSubvalue data) {
          throw new Assertion();
        }
      };
  public final Future<MortarValue> future;

  public FutureValue(Future<MortarValue> future) {
    this.future = future;
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    return get().mortarDrop(context, location);
  }

  @Override
  public EvaluateResult mortarAccess(EvaluationContext context, Location location, MortarValue field) {
    return get().mortarAccess(context, location, field);
  }

  public MortarValue get() {
    return Utils.await(future);
  }

  @Override
  public void postInit() {
    throw new Assertion();
  }

  @Override
  public Exportable type() {
    return exportableType;
  }
}
