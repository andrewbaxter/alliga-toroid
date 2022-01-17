package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;

/** All methods must be thread safe. */
public interface Logger {
  void info(TreeDumpable message);

  void warn(TreeDumpable message);
}
