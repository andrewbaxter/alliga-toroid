package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Meta;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.zarbosoft.rendaw.common.Common.uncheck;
import static java.nio.file.StandardOpenOption.CREATE;

public class CreatedFile {
  private final OutputStream outputStream;

  public CreatedFile(String path) {
    outputStream = uncheck(() -> Files.newOutputStream(Paths.get(path), CREATE));
  }

  @Meta.WrapExpose
  public void write(byte[] data) {
    uncheck(() -> outputStream.write(data));
  }
}
