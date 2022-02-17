package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.rendaw.common.ROList;

import java.lang.reflect.Constructor;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinExportableType
    implements IdentityExportableType, SingletonBuiltinExportable {
  private final Constructor constructor;

  public AutoBuiltinExportableType(Class klass) {
    constructor = uncheck(() -> klass.getConstructors()[0]);
  }

  @Override
  public SemiserialSubvalue graphSemiserializeArtifact(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return AutoGraphUtils.semiArtifact(
        semiserializer,
        spec,
        TypeInfo.fromClass(constructor.getDeclaringClass()),
        child,
        path,
        accessPath);
  }

  @Override
  public Object graphDesemiserializeArtifact(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    return AutoGraphUtils.desemiArtifact(
        context, typeDesemiserializer, constructor.getDeclaringClass(), data);
  }
}
