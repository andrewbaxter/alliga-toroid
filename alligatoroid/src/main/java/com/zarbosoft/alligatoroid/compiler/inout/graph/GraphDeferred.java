package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.UniqueId;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinArtifact;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.util.Objects;

/**
 * Either has artifact (new, pre-caching) or id. If just id, after loading
 *
 * @param <K>
 * @param <T>
 */
public class GraphDeferred<T extends Artifact> implements IdentityArtifact {
  public static final String SEMIKEY_REF = "ref";
  public static final String SEMIKEY_ID = "id";
  public static final ExportType exportType = new ExportType();
  public SemiserialSubvalueExportable ref;
  public UniqueId id;
  public T artifact;

  public static <T extends Artifact> GraphDeferred<T> create(UniqueId uniqueId, T artifact) {
    final GraphDeferred<T> out = new GraphDeferred<>();
    out.id = uniqueId;
    out.artifact = artifact;
    return out;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GraphDeferred<?> that = (GraphDeferred<?>) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public IdentityArtifactType exportableType() {
    return exportType;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeBody(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath) {
    return SemiserialRecord.create(
        new TSOrderedMap<>(
            t ->
                t.put(SemiserialString.create(SEMIKEY_REF), ref)
                    .put(
                        SemiserialString.create(SEMIKEY_ID),
                        id.graphSemiserialize(
                            importCacheId,
                            semiserializer,
                            path.mut().add(this),
                            accessPath.mut().add("id")))));
  }

  public static class ExportType implements SingletonBuiltinArtifact, IdentityArtifactType {
    @Override
    public IdentityArtifact graphDesemiserializeBody(
        ModuleCompileContext context,
        Desemiserializer typeDesemiserializer,
        SemiserialSubvalue data) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public IdentityArtifact handleRecord(SemiserialRecord s) {
              final GraphDeferred<Artifact> out = new GraphDeferred<>();
              out.ref =
                  (SemiserialSubvalueExportable)
                      context.lookupRef(
                          (SemiserialSubvalueExportable)
                              s.data.get(SemiserialString.create(SEMIKEY_REF)));
              out.id =
                  (UniqueId)
                      context.lookupRef(
                          (SemiserialSubvalueExportable)
                              s.data.get(SemiserialString.create(SEMIKEY_ID)));
              return out;
            }
          });
    }
  }
}
