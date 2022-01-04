package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;

public interface ModuleId extends TreeSerializable {
  String hash();

  boolean equal1(ModuleId other);

  <T> T dispatch(Dispatcher<T> dispatcher);

  String toString();

  interface Dispatcher<T> {
    T handleLocal(LocalModuleId id);

    T handleRemote(RemoteModuleId id);

    T handleBundle(BundleModuleSubId id);

    T handleRoot(RootModuleId id);
  }
}
