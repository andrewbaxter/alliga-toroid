package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;

public interface ModuleId extends TreeDumpable, Artifact {
  public static final Class<? extends ModuleId>[] SERIAL_UNION =
      new Class[] {
        LocalModuleId.class, RemoteModuleId.class, BundleModuleSubId.class,
      };

  String hash();

  boolean equal1(ModuleId other);

  <T> T dispatch(Dispatcher<T> dispatcher);

  String toString();

  ModuleId relative(String localPath);

  interface Dispatcher<T> {
    T handleLocal(LocalModuleId id);

    T handleRemote(RemoteModuleId id);

    T handleBundle(BundleModuleSubId id);

    T handleRoot(RootModuleId id);
  }

  public abstract class DefaultDispatcher<T> implements Dispatcher<T> {
    private final T default_;

    protected DefaultDispatcher(T default_) {
      this.default_ = default_;
    }

    @Override
    public T handleBundle(BundleModuleSubId id) {
      return default_;
    }

    @Override
    public T handleLocal(LocalModuleId id) {
      return default_;
    }

    @Override
    public T handleRemote(RemoteModuleId id) {
      return default_;
    }

    @Override
    public T handleRoot(RootModuleId id) {
      return default_;
    }
  }
}
