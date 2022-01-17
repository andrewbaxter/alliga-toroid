package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.luxem.write.Writer;

public class StderrLogger implements Logger {
  private synchronized void log(TreeDumpable message) {
    Writer outWriter = new Writer(System.err, (byte) ' ', 4);
    message.treeDump(outWriter);
  }

  @Override
  public void info(TreeDumpable message) {
    log(message);
  }

  @Override
  public void warn(TreeDumpable message) {
    log(message);
  }
}
