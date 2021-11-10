package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.ROPair;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class BundleValue implements SimpleValue {
  private final ModuleId id;
  private final String root;
  private final ImportPath fromImportPath;

  public BundleValue(ImportPath fromImportPath, ModuleId id, String root) {
    this.fromImportPath = fromImportPath;
    this.id = id;
    this.root = root;
  }

  @Override
  public EvaluateResult access(Context context, Location location, Value field) {
    WholeValue key = WholeValue.getWhole(context, location, field);
    if (key == null) return EvaluateResult.error;
    CompletableFuture<Value> res =
        context.module.compilationContext.loadModule(
            new ROPair<>(location, context.module),
            fromImportPath,
            new ImportSpec(
                new BundleModuleSubId(
                    id, Paths.get(root).resolve((String) key.concreteValue()).toString())));
    return EvaluateResult.pure(new FutureValue(res));
  }
}
