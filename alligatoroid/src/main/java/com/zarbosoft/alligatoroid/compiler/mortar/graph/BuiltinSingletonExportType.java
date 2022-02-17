package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.RootExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;

public final class BuiltinSingletonExportType implements RootExportableType {
  public static final BuiltinSingletonExportType exportType = new BuiltinSingletonExportType();

  private BuiltinSingletonExportType() {}

  @Override
  public SemiserialRef graphSemiserialize(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return SemiserialRefBuiltin.create(Meta.builtinToSemiKey.get((Exportable) child));
  }

  @Override
  public Object graphDesemiserialize(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialRef data) {
    // Only ever called for values in semiserialvalue - types can't hit this since the lookup by ref
    // happens first
    return data.dispatchRef(
        new SemiserialRef.DefaultDispatcher<>() {
          @Override
          public Object handleBuiltin(SemiserialRefBuiltin s) {
            return Meta.semiKeyToBuiltin.get(s.key);
          }
        });
  }
}
