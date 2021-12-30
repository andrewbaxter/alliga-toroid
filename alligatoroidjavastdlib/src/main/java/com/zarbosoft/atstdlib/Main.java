package com.zarbosoft.atstdlib;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.zarbosoft.alligatoroid.compiler.Main.compile;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Main {
  public static Sync sync = new Sync();

  public static void main(String[] args) {
    List<Path> generated = new ArrayList<>();

    // Collections
    generated.add(GenerateClass.generate(ArrayList.class));
    /*
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
     */

    sync.join();
    System.out.flush();
    boolean hadErrors = false;
    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    for (Path p : generated) {
      TSMap<ImportId, Module> results = compile(p.toString());
      outWriter.arrayBegin();
      for (Module value : results.values()) {
        outWriter.recordBegin();

        if (value.localSourcePath() != null) {
          outWriter.primitive("source").primitive(value.localSourcePath());
        }

        if (value.log.errors.some()) {
          hadErrors = true;
        }

        outWriter.primitive("id");
        value.spec().treeSerialize(outWriter);

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
    }
    System.out.flush();
    if (hadErrors) throw new RuntimeException("Compile errors");

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
