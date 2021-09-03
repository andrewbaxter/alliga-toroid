package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Main {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static void main(String[] args) {
    if (args.length != 1) {
      throw new RuntimeException("Need one argument, path to root module");
    }
    Path cachePath;
    String cachePath0 = System.getenv("ALLIGATOROID_CACHE");
    if (cachePath0 == null || cachePath0.isEmpty()) {
      cachePath = appDirs.user_cache_dir(false);
    } else {
      cachePath = Paths.get(cachePath0);
    }
    CompilationContext compilationContext = new CompilationContext(cachePath);
    TSMap<ModuleId, Module> modules;
    Value out;
    try {
      out =
          uncheck(
              () ->
                  compilationContext
                      .loadRootModule(Paths.get(args[0]).toAbsolutePath().normalize().toString())
                      .get());
    } finally {
      modules = compilationContext.join();
    }

    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();

    outWriter.key("modules").arrayBegin(); // TODO complex luxem keys
    for (Module value : modules.values()) {
      outWriter.recordBegin().key("id");
      value.id.serialize(outWriter);

      outWriter.key("log");
      outWriter.arrayBegin();
      for (String message : value.context.log.log) {
        outWriter.primitive(message);
      }
      outWriter.arrayEnd();

      outWriter.key("errors");
      outWriter.arrayBegin();
      for (Error error : value.context.log.errors) {
        error.serialize(outWriter);
      }
      outWriter.arrayEnd();

      outWriter.key("warnings");
      outWriter.arrayBegin();
      for (Error error : value.context.log.warnings) {
        error.serialize(outWriter);
      }
      outWriter.arrayEnd();

      outWriter.recordEnd();
    }
    outWriter.arrayEnd();

    if (out != ErrorValue.error && out != NullValue.value) {
      outWriter.key("output");
      compilationContext.cache.serializeSubValue(null, outWriter, out);
    }

    outWriter.recordEnd();
    System.out.println("");
    System.out.flush();
  }
}
