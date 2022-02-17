package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;

public interface IdentityExportableType extends ExportableType {
  @Override
  default SemiserialRef graphSemiserialize(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return semiserializer.serializeNewRef(spec, this, (Exportable) child, path, accessPath);
  }

  @Override
  default Object graphDesemiserialize(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialRef data) {
    return data.dispatch(
        new SemiserialSubvalue.DefaultDispatcher<>() {
          @Override
          public Object handleRef(SemiserialRef s) {
            return context.lookupRef(s);
          }
        });
  }

  /**
   * Presents the object structure as a hierarchy of simple values for cloning + caching.
   *
   * @return
   */
  SemiserialSubvalue graphSemiserializeArtifact(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath);

  /**
   * Takes records/tuples/values and turns them into a value with the type of this value (only
   * applicable to type values).
   *
   * @param context
   * @param typeDesemiserializer
   * @param data
   * @return
   */
  Object graphDesemiserializeArtifact(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data);
}
