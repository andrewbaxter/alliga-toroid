package com.zarbosoft.atstdlib;

import com.zarbosoft.alligatoroid.compiler.Alligatorus;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    System.out.format("\n");
    boolean hadErrors = false;
    Writer outWriter = new Writer(System.out, (byte) ' ', 4);
    outWriter.recordBegin();
    for (Path p : generated) {
      outWriter.primitive(p.toString());
      Alligatorus.Result results =
          compile(Alligatorus.defaultCachePath(), Alligatorus.rootModuleSpec(p));
      outWriter.arrayBegin();
      for (Map.Entry<ImportId, ROList<Error>> value : results.errors.entrySet()) {
        outWriter.recordBegin();

        outWriter.primitive("id");
        value.getKey().treeSerialize(outWriter);

        if (value.getValue().some()) {
          hadErrors = true;
        }

        outWriter.primitive("errors");
        outWriter.arrayBegin();
        for (Error error : value.getValue()) {
          error.treeSerialize(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.recordEnd();
      }
      outWriter.arrayEnd();
    }
    outWriter.recordEnd();
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
