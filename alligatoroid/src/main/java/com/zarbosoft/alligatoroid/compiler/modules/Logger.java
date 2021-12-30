package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;

/**
 * All methods must be thread safe.
 */
public interface Logger {
    void info(TreeSerializable message);
    void warn(TreeSerializable message);
}
