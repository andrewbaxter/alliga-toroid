package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.TypeDependencyLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Semiserializer {
  public final TSList<Error.PreError> errors;
  public final TSList<SemiserialValue> artifacts = new TSList<>();
  public final TSMap<ObjId<Exportable>, ArtifactId> artifactLookup;

  public Semiserializer(
      TSList<Error.PreError> errors, TSMap<ObjId<Exportable>, ArtifactId> artifactLookup) {
    this.errors = errors;
    this.artifactLookup = artifactLookup;
  }

  public static SemiserialModule semiserialize(
      ModuleCompileContext moduleContext, Exportable value, Location location) {
    TSList<Error.PreError> errors = new TSList<>();
    Semiserializer s = new Semiserializer(errors, moduleContext.backArtifactLookup);
    SemiserialSubvalue out =
        value
            .graphType()
            .graphSemiserialize(value, moduleContext.importId, s, new TSList<>(), new TSList<>());
    if (errors.some()) {
      for (Error.PreError error : errors) {
        moduleContext.errors.add(error.toError(location));
      }
      return null;
    }
    return SemiserialModule.create((SemiserialRef) out, s.artifacts);
  }

  public SemiserialRef serializeNewRef(
      ImportId spec,
      IdentityExportableType exportableType,
      Exportable value,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    if (path.contains(value)) {
      errors.add(new TypeDependencyLoopPre(accessPath));
      return null;
    }
    {
      ArtifactId found = artifactLookup.getOpt(new ObjId<>(value));
      if (found != null) {
        return SemiserialRefArtifact.create(found);
      }
    }
    int index = artifacts.size();
    artifacts.add(null);
    final ArtifactId id = ArtifactId.create(spec, index);
    artifactLookup.put(new ObjId<>(value), id);
    final TSList<Exportable> newPath = path.mut().add(value);
    artifacts.set(
        index,
        SemiserialValue.create(
            (SemiserialRef)
                exportableType
                    .graphType()
                    .graphSemiserialize(
                        exportableType, spec, this, newPath, accessPath.mut().add("(type)")),
            exportableType.graphSemiserializeArtifact(value, spec, this, newPath, accessPath)));
    return SemiserialRefArtifact.create(id);
  }
}
