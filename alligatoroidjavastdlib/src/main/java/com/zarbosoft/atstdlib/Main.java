package com.zarbosoft.atstdlib;

import com.zarbosoft.alligatoroid.compiler.Alligatorus;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.modules.MemoryLogger;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.Alligatorus.compile;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Main {
  public static Sync sync = new Sync();

  public static void main(String[] args) {
    List<Path> generated = new ArrayList<>();

    // Collections
    generated.add(GenerateClass.generate(ArrayList.class));
    generated.add(GenerateClass.generate(HashMap.class));
    generated.add(GenerateClass.generate(HashSet.class));

    // System
    generated.add(GenerateClass.generate(System.class));

    // File
    generated.add(GenerateClass.generate(Files.class));

    // Math
    generated.add(GenerateClass.generate(Math.class));

    // Date
    generated.add(GenerateClass.generate(ZonedDateTime.class));

    sync.join();
    System.out.flush();
    System.out.format("\n");
    boolean hadErrors = false;
    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();
    for (Path p : generated) {
      outWriter.primitive(p.toString());
      MemoryLogger logger = new MemoryLogger();
      Alligatorus.Result results =
          compile(Alligatorus.defaultCachePath(), logger, Alligatorus.rootModuleSpec(p));
      outWriter.arrayBegin();
      for (Map.Entry<ImportId, ROList<Error>> value : results.errors.entrySet()) {
        outWriter.recordBegin();

        outWriter.primitive("id");
        value.getKey().treeDump(outWriter);

        if (value.getValue().some()) {
          hadErrors = true;
        }

        outWriter.primitive("info");
        outWriter.arrayBegin();
        for (TreeDumpable message : logger.infos) {
          message.treeDump(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.primitive("warn");
        outWriter.arrayBegin();
        for (TreeDumpable message : logger.warns) {
          message.treeDump(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.primitive("errors");
        outWriter.arrayBegin();
        for (Error error : value.getValue()) {
          error.treeDump(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.recordEnd();
      }
      outWriter.arrayEnd();
    }
    outWriter.recordEnd();
    System.out.flush();
    if (hadErrors) {
        throw new RuntimeException("Compile errors");
    }

    // Bundle
    uncheck(() -> Files.deleteIfExists(Paths.get("bundle.zip")));
    uncheck(
        () ->
            new ProcessBuilder("zip", "-r", "../bundle.zip", ".")
                .directory(Paths.get("out").toFile())
                .start()
                .waitFor());
  }
}
