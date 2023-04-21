package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExportNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Semiserializer {
  public final TSList<Error.PreError> errors;
  public final TSList<SemiserialExportable> artifacts = new TSList<>();
  public final TSMap<ObjId<Object>, ArtifactId> artifactLookup;

  public Semiserializer(
      TSList<Error.PreError> errors, TSMap<ObjId<Object>, ArtifactId> artifactLookup) {
    this.errors = errors;
    this.artifactLookup = artifactLookup;
  }

  /**
   * Entrypoint to semiserialization; sets up serializer, deals with errors, and just calls
   * `exportableType.semiserializeValue`
   */
  public static SemiserialModule semiserialize(
      ModuleCompileContext moduleContext, Object value, Location location) {
    TSList<Error.PreError> errors = new TSList<>();
    Semiserializer s = new Semiserializer(errors, moduleContext.backArtifactLookup);
    Exporter exporter = null;
    if (value instanceof Exportable) {
      exporter = ((Exportable) value).exporter();
    }
    if (exporter == null) {
      Exporter et0 = StaticAutogen.detachedExportableTypeLookup.get(value.getClass());
      if (et0 != null) {
        exporter = et0;
      }
    }
    if (exporter == null) {
      moduleContext.errors.add(new ExportNotSupported(location));
      return null;
    }
    SemiserialRef out =
        exporter.semiserializeValue(
            moduleContext.importCacheId, s, new TSList<>(), new TSList<>(), value);
    if (errors.some()) {
      for (Error.PreError error : errors) {
        moduleContext.errors.add(error.toError(location));
      }
      return null;
    }
    return SemiserialModule.create(out, s.artifacts);
  }
}
