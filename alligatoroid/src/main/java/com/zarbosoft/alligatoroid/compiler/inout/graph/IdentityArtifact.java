package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.TypeDependencyLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public interface IdentityArtifact extends Artifact {
    /** Called after deferred initialization in graph desemiserialization. */
    default void postInit() {}
  IdentityArtifactType exportableType();

  @Override
  default SemiserialSubvalueExportable graphSemiserialize(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath) {
      IdentityArtifactType exportableType = exportableType();
      {
        ArtifactId found = semiserializer.artifactLookup.getOpt(new ObjId<>(this));
        if (found != null) {
          return SemiserialSubvalueExportableIdentityRef.create(found);
        }
      }
      if (path.contains(this)) {
        semiserializer.errors.add(new TypeDependencyLoopPre(accessPath));
        return null;
      }
      int index = semiserializer.artifacts.size();
      semiserializer.artifacts.add(null);
      final ArtifactId id = ArtifactId.create(importCacheId, index);
      semiserializer.artifactLookup.put(new ObjId<>(this), id);
      final TSList<Artifact> newPath = path.mut().add(this);
      semiserializer.artifacts.set(
          index,
          SemiserialExportableIdentityBody.create(
              exportableType.graphSemiserialize(
                      importCacheId, semiserializer, newPath, accessPath.mut().add("(type)")),
              graphSemiserializeBody(
                      importCacheId, semiserializer, newPath, accessPath)));
      return SemiserialSubvalueExportableIdentityRef.create(id);
  }

  SemiserialSubvalue graphSemiserializeBody(long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath);
}
