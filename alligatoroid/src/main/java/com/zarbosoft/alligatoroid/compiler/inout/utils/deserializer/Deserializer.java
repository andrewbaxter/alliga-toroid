package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.CacheUnexpectedPre;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeIncompleteFilePre;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
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

  public static <C> void deserialize(
      C context, TSList<Error> errors, Path path, TSList<State> stack) {
    uncheck(
        () -> {
          try (InputStream stream = Files.newInputStream(path)) {
            deserialize(context, errors, path.toString(), stream, stack);
          }
        });
  }

  public static <C> void deserialize(
      C context, TSList<Error> errors, String path, InputStream source, TSList<State> stack) {
    // TODO luxem path
    BufferedReader reader =
        new BufferedReader() {
          LuxemPathBuilder luxemPath = new LuxemArrayPathBuilder(null);

          @Override
          protected void eatRecordBegin() {
            luxemPath = luxemPath.pushRecordOpen();
            stack.last().eatRecordBegin(context, errors, stack, luxemPath);
          }

          @Override
          protected void eatArrayBegin() {
            luxemPath = luxemPath.pushArrayOpen();
            stack.last().eatArrayBegin(context, errors, stack, luxemPath);
          }

          @Override
          protected void eatArrayEnd() {
            stack.last().eatArrayEnd(context, errors, stack, luxemPath);
            luxemPath = luxemPath.pop();
          }

          @Override
          protected void eatRecordEnd() {
            stack.last().eatRecordEnd(context, errors, stack, luxemPath);
            luxemPath = luxemPath.pop();
          }

          @Override
          protected void eatType(String value) {
            stack.last().eatType(context, errors, stack, luxemPath, value);
            luxemPath = luxemPath.type();
          }

          @Override
          protected void eatPrimitive(String value) {
            stack.last().eatPrimitive(context, errors, stack, luxemPath, value);
            luxemPath = luxemPath.value();
          }
        };
    try {
      reader.feed(source);
    } catch (Exception e) {
      throw new CacheUnexpectedPre(path, e);
    }
    if (stack.some()) {
      throw new DeserializeIncompleteFilePre(path);
    }
  }

  public static class ErrorRet {}
}
