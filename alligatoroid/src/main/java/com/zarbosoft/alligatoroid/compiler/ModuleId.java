package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;

public interface ModuleId extends TreeSerializable, GraphSerializable {
  String hash();

  boolean equal1(ModuleId other);

  <T> T dispatch(Dispatcher<T> dispatcher);

  interface Dispatcher<T> {
    T handle(LocalModuleId id);
    T handle(RemoteModuleId id);
    T handle(RemoteModuleSubId id);
  }
}
