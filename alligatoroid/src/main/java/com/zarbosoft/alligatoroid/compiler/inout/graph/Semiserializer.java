package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Semiserializer {
  public final TSList<Error.PreError> errors;
  public final TSList<SemiserialExportableIdentityBody> artifacts = new TSList<>();
  public final TSMap<ObjId<IdentityArtifact>, ArtifactId> artifactLookup;

  public Semiserializer(
      TSList<Error.PreError> errors, TSMap<ObjId<IdentityArtifact>, ArtifactId> artifactLookup) {
    this.errors = errors;
    this.artifactLookup = artifactLookup;
  }

  public static SemiserialModule semiserialize(
          ModuleCompileContext moduleContext, Artifact value, Location location) {
    TSList<Error.PreError> errors = new TSList<>();
    Semiserializer s = new Semiserializer(errors, moduleContext.backArtifactLookup);
    SemiserialSubvalueExportable out =
        value.graphSemiserialize(moduleContext.importCacheId, s, new TSList<>(), new TSList<>());
    if (errors.some()) {
      for (Error.PreError error : errors) {
        moduleContext.errors.add(error.toError(location));
      }
      return null;
    }
    return SemiserialModule.create(out, s.artifacts);
  }
}
