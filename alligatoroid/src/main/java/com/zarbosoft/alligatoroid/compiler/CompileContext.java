package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.UniqueId;
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

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class CompileContext {
  public final ConcurrentHashMap<ImportId, ROList<Error>> moduleErrors;
  public final ConcurrentHashMap<ImportId, ROList<String>> moduleLog;
  public final ConcurrentHashMap<UniqueId, Void> loadedDefinitionSets;
  public final ConcurrentHashMap<String, Definition> loadedDefinitions;
  public final ClassLoader classLoader;
  public final LocalDependents dependents;
  public final Logger logger;
  public final Threads threads = new Threads();
  public final Modules modules;
  public final Sources sources;
  public final ConcurrentHashMap<ModuleId, Path> localSources;
  public ConcurrentHashMap<ModuleId, TSMap<Location, ROSetRef<String>>> traceModuleStringFields;

  public CompileContext(Path cacheRoot, Logger logger, ImportId rootImportId) {
    loadedDefinitions = new ConcurrentHashMap<>();
    loadedDefinitionSets = new ConcurrentHashMap<>();
    moduleLog = new ConcurrentHashMap<>();
    moduleErrors = new ConcurrentHashMap<>();
    localSources = new ConcurrentHashMap<>();
    traceModuleStringFields = new ConcurrentHashMap<>();
    modules = new Modules(new ModuleDiskCache(cacheRoot.resolve("modules"), new ModuleCompiler()));
    sources = new Sources(new SourceDiskCache(cacheRoot.resolve("sources")));
    this.logger = logger;
    dependents = new LocalDependents(this.logger, rootImportId.moduleId, cacheRoot);
    classLoader =
        new ClassLoader(ClassLoader.getSystemClassLoader()) {
          @Override
          public Class<?> loadClass(String name) throws ClassNotFoundException {
            Definition found = loadedDefinitions.get(name);
            if (found != null) {
              final Class<?> out = defineClass(name, found.bytecode(), 0, found.bytecode().length);
              found.postLoad(out);
              return out;
            }
            return super.loadClass(name);
          }
        };
  }

  public Class loadRootClass(String loadName, byte[] bytecode) {
    return uncheck(
        () ->
            new ClassLoader(classLoader) {
              @Override
              public Class<?> loadClass(String name)
                  throws ClassNotFoundException {
                if (name.equals(loadName)) {
                  return defineClass(loadName, bytecode, 0, bytecode.length);
                }
                return super.loadClass(name);
              }
            }.loadClass(loadName));
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
            if (entries == null) {
              entries = new TSMap<>();
            }
            entries.put(location, strings);
            return entries;
          }
        });
  }

  public interface Definition {
    byte[] bytecode();

    void postLoad(Class klass);
  }
}
