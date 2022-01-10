package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.modules.LocalLogger;
import com.zarbosoft.alligatoroid.compiler.modules.Logger;
import com.zarbosoft.alligatoroid.compiler.modules.Modules;
import com.zarbosoft.alligatoroid.compiler.modules.Sources;
import com.zarbosoft.alligatoroid.compiler.modules.modulecompiler.ModuleCompiler;
import com.zarbosoft.alligatoroid.compiler.modules.modulediskcache.ModuleDiskCache;
import com.zarbosoft.alligatoroid.compiler.modules.sourcediskcache.SourceDiskCache;
import com.zarbosoft.rendaw.common.ROList;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class CompileContext {
  public final ConcurrentHashMap<ImportId, ROList<Error>> moduleErrors = new ConcurrentHashMap<>();
  public final Logger logger = new LocalLogger();
  public final Threads threads = new Threads();
  public final Modules modules;
  public final Sources sources;

  public final ConcurrentHashMap<ImportId, Path> localSources = new ConcurrentHashMap<>();

  public CompileContext(Path cacheRoot) {
    modules = new Modules(new ModuleDiskCache(cacheRoot.resolve("modules"), new ModuleCompiler()));
    sources = new Sources(new SourceDiskCache(cacheRoot.resolve("sources")));
  }
}
