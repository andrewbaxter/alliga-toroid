package com.zarbosoft.atstdlib;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.zarbosoft.alligatoroid.compiler.Main.compile;

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
      TSMap<ImportSpec, Module> results = compile(p.toString());
      outWriter.arrayBegin();
      for (Module value : results.values()) {
        outWriter.recordBegin();

        if (value.sourcePath != null) {
          outWriter.primitive("source").primitive(value.sourcePath);
        }

        if (value.log.errors.some()) {
          hadErrors = true;
        }

        outWriter.primitive("id");
        value.id.serialize(outWriter);

        outWriter.primitive("log");
        outWriter.arrayBegin();
        for (String message : value.log.log) {
          outWriter.primitive(message);
        }
        outWriter.arrayEnd();

        outWriter.primitive("errors");
        outWriter.arrayBegin();
        for (Error error : value.log.errors) {
          error.serialize(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.primitive("warnings");
        outWriter.arrayBegin();
        for (Error error : value.log.warnings) {
          error.serialize(outWriter);
        }
        outWriter.arrayEnd();

        outWriter.recordEnd();
      }
      outWriter.arrayEnd();
    }
    System.out.flush();
    if (hadErrors) throw new RuntimeException("Compile errors");
  }
}
