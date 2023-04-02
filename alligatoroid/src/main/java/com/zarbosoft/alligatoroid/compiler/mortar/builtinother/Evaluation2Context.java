package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.DefinitionSet;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.concurrent.CompletableFuture;

public class Evaluation2Context {
  public final TSList<String> log;
  public final TSList<ROPair<Location, CompletableFuture>> deferredErrors = new TSList<>();
  public final ModuleCompileContext
      moduleContext; // TODO split getModule into new class and not store moduleContext

  public Evaluation2Context(TSList<String> log, ModuleCompileContext moduleContext) {
    this.log = log;
    this.moduleContext = moduleContext;
  }

  @StaticAutogen.WrapExpose
  public void log(String message) {
    log.add(message);
  }

  @StaticAutogen.WrapExpose
  public DefinitionSet define(Location location) {
    return DefinitionSet.create(
        location, moduleContext.importCacheId, moduleContext.nextLocalId.incrementAndGet());
  }

  @StaticAutogen.WrapExpose
  public ImportId modLocal(StaticAutogen.BuiltinContext context, String path) {
    return ImportId.create(context.context.importId.moduleId.relative(path));
  }

  @StaticAutogen.WrapExpose
  public FutureValue _import(Location location, ImportId importId) {
    CompletableFuture<Value> importResult =
        moduleContext.getModule(moduleContext.compileContext.modules.getCacheId(importId));
    deferredErrors.add(new ROPair<>(location, importResult));
    return new FutureValue(
        importResult.exceptionally(
            e -> {
              return ErrorValue.value;
            }));
  }
}
