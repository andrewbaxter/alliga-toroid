package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static TSMap<ImportSpec, Module> compile(String path) {
    Path cachePath;
    String cachePath0 = System.getenv("ALLIGATOROID_CACHE");
    if (cachePath0 == null || cachePath0.isEmpty()) {
      cachePath = appDirs.user_cache_dir(false);
    } else {
      cachePath = Paths.get(cachePath0);
    }
    CompilationContext compilationContext = new CompilationContext(cachePath);
    TSMap<ImportSpec, Module> modules;
    Value out;
    try {
      compilationContext.loadRootModule(Paths.get(path).toAbsolutePath().normalize().toString());
    } finally {
      modules = compilationContext.join();
    }
    return modules;
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      throw new RuntimeException("Need one argument, path to root module");
    }
    TSMap<ImportSpec, Module> modules = compile(args[0]);

    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();

    outWriter.key("modules").arrayBegin(); // TODO complex luxem keys
    for (Module value : modules.values()) {
      if (value.sourcePath != null) {
        outWriter.key("source").primitive(value.sourcePath);
      }

      outWriter.recordBegin().key("id");
      value.id.serialize(outWriter);

      outWriter.key("log");
      outWriter.arrayBegin();
      for (String message : value.log.log) {
        outWriter.primitive(message);
      }
      outWriter.arrayEnd();

      outWriter.key("errors");
      outWriter.arrayBegin();
      for (Error error : value.log.errors) {
        error.serialize(outWriter);
      }
      outWriter.arrayEnd();

      outWriter.key("warnings");
      outWriter.arrayBegin();
      for (Error error : value.log.warnings) {
        error.serialize(outWriter);
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
