package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.Meta;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zarbosoft.rendaw.common.Common.uncheck;
import static java.nio.file.StandardOpenOption.CREATE;

public class CreatedFile {
  private final OutputStream outputStream;

  public CreatedFile(String path0) {
    final Path path = Paths.get(path0);
    uncheck(() -> Files.createDirectories(path.getParent()));
    outputStream = uncheck(() -> Files.newOutputStream(path, CREATE));
  }

  @Meta.WrapExpose
  public void write(byte[] data) {
    uncheck(() -> outputStream.write(data));
  }
}
