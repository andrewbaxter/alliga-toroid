package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;

public interface ModuleId extends TreeSerializable {
  String hash();

  boolean equal1(ModuleId other);

  <T> T dispatch(Dispatcher<T> dispatcher);

  String toString();

  interface Dispatcher<T> {
    T handle(LocalModuleId id);

    T handle(RemoteModuleId id);

    T handle(BundleModuleSubId id);
  }
}
