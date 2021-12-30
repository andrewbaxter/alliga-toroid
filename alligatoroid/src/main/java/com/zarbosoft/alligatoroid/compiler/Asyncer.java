package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.concurrent.CompletableFuture;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Asyncer<K, T> {
  private final TSMap<K, CompletableFuture<T>> map = new TSMap<>();

  public synchronized CompletableFuture<T> get(Threads threads, K key, Common.UncheckedSupplier<T> s) {
    CompletableFuture<T> got = map.getOpt(key);
    if (got == null) {
      final CompletableFuture<T> future = new CompletableFuture<>();
      map.put(key, future);
      threads.fork(() -> future.completeAsync(() -> Common.<T>uncheck(s)));
      got = future;
    }
    return got;
  }

  public synchronized T getSync(Threads threads, K key, Common.UncheckedSupplier<T> s) {
    return uncheck(() -> get(threads,key, s).get());
  }
}
