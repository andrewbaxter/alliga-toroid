package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.UniqueId;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.util.Objects;

/**
 * Either has artifact (new, pre-caching) or id. If just id, after loading
 *
 * @param <K>
 * @param <T>
 */
public class GraphDeferred<T> implements Exportable {
  public static final String SEMIKEY_REF = "ref";
  public static final String SEMIKEY_ID = "id";
  public SemiserialRef ref;
  public UniqueId id;
  public T artifact;

  public static <T> GraphDeferred<T> create(UniqueId uniqueId, T artifact) {
    final GraphDeferred<T> out = new GraphDeferred<>();
    out.id = uniqueId;
    out.artifact = artifact;
    return out;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GraphDeferred<?> that = (GraphDeferred<?>) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter exporter() {
    return new Exporter(this);
  }

  public static class Exporter<T>
      implements BuiltinAutoExportable,
          com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter {
    private final GraphDeferred<T> graphDeferred;

    public Exporter(GraphDeferred<T> graphDeferred) {
      this.graphDeferred = graphDeferred;
    }

    @Override
    public SemiserialSubvalue graphSemiserializeBody(
        long importCacheId,
        Semiserializer semiserializer,
        ROList<Object> path,
        ROList<String> accessPath,
        Object value) {
      return SemiserialRecord.create(
          new TSOrderedMap<>(
              t -> {
                final com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter
                        idExporter = graphDeferred.id.exporter();
                t.put(SemiserialString.create(SEMIKEY_REF), graphDeferred.ref)
                    .put(
                        SemiserialString.create(SEMIKEY_ID),
                        idExporter.semiserializeValue(
                            importCacheId,
                            semiserializer,
                            path.mut().add(this),
                            accessPath.mut().add("id"),
                                idExporter));
              }));
    }

    @Override
    public Object graphDesemiserializeBody(
        ModuleCompileContext context,
        Desemiserializer typeDesemiserializer,
        SemiserialSubvalue data) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Exportable handleRecord(SemiserialRecord s) {
              final GraphDeferred<Exportable> out = new GraphDeferred<>();
              out.ref =
                  (SemiserialRef)
                      context.lookupRef(
                          (SemiserialRef) s.data.get(SemiserialString.create(SEMIKEY_REF)));
              out.id =
                  (UniqueId)
                      context.lookupRef(
                          (SemiserialRef) s.data.get(SemiserialString.create(SEMIKEY_ID)));
              return out;
            }
          });
    }
  }
}
