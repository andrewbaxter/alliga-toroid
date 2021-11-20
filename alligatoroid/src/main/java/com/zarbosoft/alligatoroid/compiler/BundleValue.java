package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class BundleValue implements SimpleValue, GraphSerializable {
  private static final String GRAPH_KEY_ROOT = "root";
  private static final String GRAPH_KEY_ID = "id";
  private final ImportSpec id;
  private final String root;

  public BundleValue(ImportSpec id, String root) {
    this.id = id;
    this.root = root;
  }

  public static BundleValue graphDeserialize(Record data) {
    return new BundleValue(
        (ImportSpec) data.data.get(GRAPH_KEY_ID), (String) data.data.get(GRAPH_KEY_ROOT));
  }

  @Override
  public EvaluateResult access(Context context, Location location, Value field) {
    WholeValue key = WholeValue.getWhole(context, location, field);
    if (key == null) return EvaluateResult.error;
    CompletableFuture<Value> res =
        context.module.compilationContext.loadModule(
            new ROPair<>(location, context.module),
            context.module.importPath,
            new ImportSpec(
                new BundleModuleSubId(
                    id.moduleId,
                    Paths.get(root).resolve((String) key.concreteValue()).toString())));
    return EvaluateResult.pure(new FutureValue(res));
  }

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>(s -> s.put(GRAPH_KEY_ID, id).put(GRAPH_KEY_ROOT, root)));
  }
}
