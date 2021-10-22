package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

import java.nio.file.Paths;

public class BundleValue implements SimpleValue {
  private final RemoteModuleId id;
  private final String root;
  private final ImportPath fromImportPath;

  public BundleValue(ImportPath fromImportPath, RemoteModuleId id, String root) {
    this.fromImportPath = fromImportPath;
    this.id = id;
    this.root = root;
  }

  @Override
  public EvaluateResult access(Context context, Location location, Value field) {
    WholeValue key = WholeValue.getWhole(context, location, field);
    if (key == null) return EvaluateResult.error;
    CompilationContext.ImportResult res =
        context.module.compilationContext.loadModule(
            fromImportPath,
            new ImportSpec(
                new RemoteModuleSubId(
                    id, Paths.get(root).resolve((String) key.concreteValue()).toString())));
    return EvaluateResult.pure(new FutureValue(res.value));
  }
}
