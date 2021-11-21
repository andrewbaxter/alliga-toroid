package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static TSMap<ImportSpec, Module> compile(ImportSpec spec) {
    Path cachePath;
    String cachePath0 = System.getenv("ALLIGATOROID_CACHE");
    if (cachePath0 == null || cachePath0.isEmpty()) {
      cachePath = appDirs.user_cache_dir(false);
    } else {
      cachePath = Paths.get(cachePath0);
    }
    CompilationContext compilationContext = new CompilationContext(cachePath);
    TSMap<ImportSpec, Module> modules;
    try {
      compilationContext.loadRootModule(spec);
    } finally {
      modules = compilationContext.join();
    }
    return modules;
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      throw new RuntimeException("Need one argument, path to root module");
    }
    TSMap<ImportSpec, Module> modules =
        compile(CompilationContext.rootModuleSpec(Paths.get(args[0])));

    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();

    outWriter.primitive("modules").arrayBegin();
    for (Module value : modules.values()) {
      if (value.sourcePath != null) {
        outWriter.primitive("source").primitive(value.sourcePath);
      }

      outWriter.recordBegin().primitive("id");
      value.spec.treeSerialize(outWriter);

      outWriter.primitive("log");
      outWriter.arrayBegin();
      for (String message : value.log.log) {
        outWriter.primitive(message);
      }
      outWriter.arrayEnd();

      outWriter.primitive("errors");
      outWriter.arrayBegin();
      for (Error error : value.log.errors) {
        error.treeSerialize(outWriter);
      }
      outWriter.arrayEnd();

      outWriter.primitive("warnings");
      outWriter.arrayBegin();
      for (Error error : value.log.warnings) {
        error.treeSerialize(outWriter);
      }
      outWriter.arrayEnd();

      outWriter.recordEnd();
    }
    outWriter.arrayEnd();

    outWriter.recordEnd();
    System.out.println("");
    System.out.flush();
  }
}
