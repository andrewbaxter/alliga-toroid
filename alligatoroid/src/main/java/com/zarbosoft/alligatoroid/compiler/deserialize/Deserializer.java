package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.BufferedReader;
import com.zarbosoft.luxem.read.path.LuxemArrayPathBuilder;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Deserializer {
  public static final ErrorRet errorRet = new ErrorRet();

  public static void deserialize(TSList<Error> errors, Path path, TSList<State> stack) {
    uncheck(
        () -> {
          try (InputStream stream = Files.newInputStream(path)) {
            deserialize(errors, path.toString(), stream, stack);
          }
        });
  }

  public static void deserialize(
      TSList<Error> errors, String path, InputStream source, TSList<State> stack) {
    // TODO luxem path
    BufferedReader reader =
        new BufferedReader() {
          LuxemPathBuilder luxemPath = new LuxemArrayPathBuilder(null);

          @Override
          protected void eatRecordBegin() {
            luxemPath = luxemPath.pushRecordOpen();
            stack.last().eatRecordBegin(errors, stack, luxemPath);
          }

          @Override
          protected void eatArrayBegin() {
            luxemPath = luxemPath.pushArrayOpen();
            stack.last().eatArrayBegin(errors, stack, luxemPath);
          }

          @Override
          protected void eatArrayEnd() {
            luxemPath = luxemPath.pop();
            stack.last().eatArrayEnd(errors, stack, luxemPath);
          }

          @Override
          protected void eatRecordEnd() {
            luxemPath = luxemPath.pop();
            stack.last().eatRecordEnd(errors, stack, luxemPath);
          }

          @Override
          protected void eatType(String value) {
            luxemPath = luxemPath.type();
            stack.last().eatType(errors, stack, luxemPath, value);
          }

          @Override
          protected void eatPrimitive(String value) {
            luxemPath = luxemPath.value();
            stack.last().eatPrimitive(errors, stack, luxemPath, value);
          }
        };
    uncheck(() -> reader.feed(source));
    if (stack.some()) {
      throw new Error.PreDeserializeIncompleteFile(path);
    }
  }

  public static class ErrorRet {}
}
