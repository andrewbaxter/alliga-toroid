package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class BundleValue implements SimpleValue, AutoBuiltinExportable, LeafExportable {
  private static final String GRAPH_KEY_ROOT = "root";
  private static final String GRAPH_KEY_ID = "id";
  public final String root;
  public ImportId id;

  public BundleValue(ImportId id, String root) {
    this.id = id;
    this.root = root;
  }

  public static BundleValue graphDeserialize(Record data) {
    return new BundleValue(
        (ImportId) data.data.get(GRAPH_KEY_ID), (String) data.data.get(GRAPH_KEY_ROOT));
  }

  @Override
  public EvaluateResult mortarAccess(EvaluationContext context, Location location, MortarValue field) {
    WholeValue key = WholeValue.getWhole(context, location, field);
    if (key == null) return EvaluateResult.error;

    CompletableFuture<MortarValue> importResult =
        context.moduleContext.getModule(
            new ImportId(
                new BundleModuleSubId(
                    id.moduleId,
                    Paths.get(root).resolve((String) key.concreteValue()).toString())));
    return EvaluateResult.pure(new FutureValue(importResult));
  }
}
