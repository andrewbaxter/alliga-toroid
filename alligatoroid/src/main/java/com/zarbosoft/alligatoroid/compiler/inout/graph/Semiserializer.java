package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ExportNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarType;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.jetbrains.annotations.Nullable;

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
  public static SemiserialModule rootSemiserialize(
      ModuleCompileContext moduleContext, ROPair<MortarType, Object> value, Location location) {
    TSList<Error.PreError> errors = new TSList<>();
    Semiserializer s = new Semiserializer(errors, moduleContext.backArtifactLookup);
    SemiserialRef valueTypeRef = rootSemiserializeOne(moduleContext, value.first, location, s);
    if (valueTypeRef == null) {
      return null;
    }
    SemiserialRef valueRef = rootSemiserializeOne(moduleContext, value.second, location, s);
    if (valueRef == null) {
      return null;
    }
    if (errors.some()) {
      for (Error.PreError error : errors) {
        moduleContext.errors.add(error.toError(location));
      }
      return null;
    }
    return SemiserialModule.create(valueTypeRef, valueRef, s.artifacts);
  }

  @Nullable
  private static SemiserialRef rootSemiserializeOne(
      ModuleCompileContext moduleContext, Object value, Location location, Semiserializer s) {
    Exporter valueExporter = null;
    if (value instanceof Exportable) {
      valueExporter = ((Exportable) value).exporter();
    } else if (value == null) {
      valueExporter = StaticAutogen.detachedExportableTypeLookup.get(void.class);
    } else {
      Exporter et0 = StaticAutogen.detachedExportableTypeLookup.get(value.getClass());
      if (et0 != null) {
        valueExporter = et0;
      }
    }
    if (valueExporter == null) {
      moduleContext.errors.add(new ExportNotSupported(location));
      return null;
    }
    SemiserialRef valueRef =
        valueExporter.semiserializeValue(
            moduleContext.importCacheId, s, new TSList<>(), new TSList<>(), value);
    return valueRef;
  }
}
