package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Logger;
import com.zarbosoft.alligatoroid.compiler.modules.Modules;
import com.zarbosoft.alligatoroid.compiler.modules.Sources;
import com.zarbosoft.alligatoroid.compiler.modules.modulecompiler.ModuleCompiler;
import com.zarbosoft.alligatoroid.compiler.modules.modulediskcache.ModuleDiskCache;
import com.zarbosoft.alligatoroid.compiler.modules.sourcediskcache.SourceDiskCache;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class CompileContext {
  public final ConcurrentHashMap<ImportId, ROList<Error>> moduleErrors = new ConcurrentHashMap<>();
  public final LocalDependents dependents;
  public final Logger logger;
  public final Threads threads = new Threads();
  public final Modules modules;
  public final Sources sources;

  public final ConcurrentHashMap<ModuleId, Path> localSources = new ConcurrentHashMap<>();
  public ConcurrentHashMap<ModuleId, TSMap<Location, ROSetRef<String>>> traceModuleStringFields =
      new ConcurrentHashMap<>();

  public CompileContext(Path cacheRoot, Logger logger, ImportId rootImportId) {
    modules = new Modules(new ModuleDiskCache(cacheRoot.resolve("modules"), new ModuleCompiler()));
    sources = new Sources(new SourceDiskCache(cacheRoot.resolve("sources")));
    this.logger = logger;
    dependents = new LocalDependents(this.logger, rootImportId.moduleId, cacheRoot);
  }

  public void addTraceModuleStringFields(
      ModuleId moduleId, Location location, ROSetRef<String> strings) {
    traceModuleStringFields.compute(
        moduleId,
        new BiFunction<
            ModuleId, TSMap<Location, ROSetRef<String>>, TSMap<Location, ROSetRef<String>>>() {
          @Override
          public TSMap<Location, ROSetRef<String>> apply(
              ModuleId moduleId, TSMap<Location, ROSetRef<String>> entries) {
            if (entries == null) entries = new TSMap<>();
            entries.put(location, strings);
            return entries;
          }
        });
  }
}
