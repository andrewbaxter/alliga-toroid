package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.TypeDependencyLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

/**
 * An identity exportable provides a flattening point during semiserialization. A referenced
 * identity exportable will be serialized as a reference in the referrer, and then added to the top
 * level array of artifacts. Multiple referrers will refer to the same artifact (it only gets
 * semiserialized once)
 */
public interface IdentityExportableType extends ExportableType {
  IdentityExportable graphDesemiserializeBody(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data);

  @Override
  default SemiserialSubvalue graphSemiserializeValue(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Object> path,
      ROList<String> accessPath,
      Object value) {
    IdentityExportable exportable = (IdentityExportable) value;
    {
      ArtifactId found = semiserializer.artifactLookup.getOpt(new ObjId<>(exportable));
      if (found != null) {
        return SemiserialSubvalueRefIdentity.create(found);
      }
    }
    if (path.contains(exportable)) {
      semiserializer.errors.add(new TypeDependencyLoopPre(accessPath));
      return null;
    }
    int index = semiserializer.artifacts.size();
    semiserializer.artifacts.add(null);
    final ArtifactId id = ArtifactId.create(importCacheId, index);
    semiserializer.artifactLookup.put(new ObjId<>(exportable), id);
    final TSList<Object> newPath = path.mut().add(exportable);
    semiserializer.artifacts.set(
        index,
        SemiserialExportableIdentityBody.create(
            exportableType()
                .graphSemiserializeValue(
                    importCacheId, semiserializer, newPath, accessPath.mut().add("(type)"), this),
            graphSemiserializeBody(importCacheId, semiserializer, newPath, accessPath, value)));
    return SemiserialSubvalueRefIdentity.create(id);
  }

  SemiserialSubvalue graphSemiserializeBody(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Object> path,
      ROList<String> accessPath,
      Object value);

  ExportableType exportableType();
}
