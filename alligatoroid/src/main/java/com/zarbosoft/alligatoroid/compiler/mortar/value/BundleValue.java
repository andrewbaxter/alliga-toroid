package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstString;

public class BundleValue implements Value, AutoBuiltinExportable, Exportable {
  private static final String GRAPH_KEY_ROOT = "root";
  private static final String GRAPH_KEY_ID = "id";
  @Param public String root;
  @Param public ImportId id;

  public static BundleValue graphDeserialize(Record data) {
    return create((ImportId) data.data.get(GRAPH_KEY_ID), (String) data.data.get(GRAPH_KEY_ROOT));
  }

  public static BundleValue create(ImportId id, String root) {
    final BundleValue out = new BundleValue();
    out.id = id;
    out.root = root;
    out.postInit();
    return out;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    final String key = assertConstString(context, location, field);
    if (key == null) return EvaluateResult.error;
    CompletableFuture<Value> importResult =
        context.moduleContext.getModule(
            ImportId.create(
                BundleModuleSubId.create(id.moduleId, Paths.get(root).resolve(key).toString())));
    return EvaluateResult.pure(new FutureValue(importResult));
  }
}
