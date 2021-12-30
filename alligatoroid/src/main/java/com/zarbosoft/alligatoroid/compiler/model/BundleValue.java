package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoGraphBuiltinValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class BundleValue implements SimpleValue, AutoGraphBuiltinValue {
  private static final String GRAPH_KEY_ROOT = "root";
  private static final String GRAPH_KEY_ID = "id";
  private final ImportId id;
  private final String root;

  public BundleValue(ImportId id, String root) {
    this.id = id;
    this.root = root;
  }

  public static BundleValue graphDeserialize(Record data) {
    return new BundleValue(
        (ImportId) data.data.get(GRAPH_KEY_ID), (String) data.data.get(GRAPH_KEY_ROOT));
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    WholeValue key = WholeValue.getWhole(context, location, field);
    if (key == null) return EvaluateResult.error;

    CompletableFuture<Value> importResult =
        context.moduleContext.compileContext.modules.get(
            context.moduleContext.compileContext,
            context.moduleContext.importPath,
            new ImportId(
                new BundleModuleSubId(
                    id.moduleId,
                    Paths.get(root).resolve((String) key.concreteValue()).toString())));
    return EvaluateResult.pure(new FutureValue(importResult));
  }
}
