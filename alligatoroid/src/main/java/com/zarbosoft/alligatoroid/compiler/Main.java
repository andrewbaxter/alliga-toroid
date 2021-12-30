package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.Alligatorus.appDirs;

public class Main {
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
    Alligatorus.Result result =
        Alligatorus.compile(cachePath, Alligatorus.rootModuleSpec(Paths.get(args[0])));

    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();

    outWriter.primitive("modules").arrayBegin();
    for (Map.Entry<ImportId, ROList<Error>> value : result.errors.entrySet()) {
      Path localSource = result.localSources.get(value.getKey());
      if (localSource != null) {
        outWriter.primitive("source").primitive(value.toString());
      }

      outWriter.recordBegin().primitive("id");
      value.getKey().treeSerialize(outWriter);

      outWriter.primitive("errors");
      outWriter.arrayBegin();
      for (Error error : value.getValue()) {
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
