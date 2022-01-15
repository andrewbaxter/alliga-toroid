package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.luxem.write.Writer;

public class LocalLogger implements Logger {
  private synchronized void log(TreeSerializable message) {
    Writer outWriter = new Writer(System.err, (byte) ' ', 4);
    message.treeSerialize(outWriter);
  }

  @Override
  public void info(TreeSerializable message) {
    log(message);
  }

  @Override
  public void warn(TreeSerializable message) {
    log(message);
  }
}
