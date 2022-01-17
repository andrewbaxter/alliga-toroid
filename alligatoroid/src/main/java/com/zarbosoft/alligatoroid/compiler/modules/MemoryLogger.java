package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.rendaw.common.TSList;

public class MemoryLogger implements Logger {
  public TSList<TreeDumpable> infos = new TSList<>();
  public TSList<TreeDumpable> warns = new TSList<>();

  @Override
  public void info(TreeDumpable message) {
    synchronized (this) {
      infos.add(message);
    }
  }

  @Override
  public void warn(TreeDumpable message) {
    synchronized (this) {
      warns.add(message);
    }
  }
}
