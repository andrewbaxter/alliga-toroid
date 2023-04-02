package com.zarbosoft.atstdlib;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Sync {
  private final Map<Class, Future> seen = new HashMap<Class, Future>();
  ExecutorService executor = Executors.newFixedThreadPool(64);
  private int outstanding = 0;

  public synchronized Future push(GenerateClass k) {
    {
      Future found = seen.get(k.klass);
      if (found != null) {
          return found;
      }
    }
    CompletableFuture future = new CompletableFuture();
    seen.put(k.klass, future);
    outstanding += 1;
    executor.submit(
        () -> {
          k.generate();
          done();
          future.complete(null);
        });
    return future;
  }

  private synchronized void done() {
    outstanding -= 1;
    if (outstanding == 0) {
      executor.shutdown();
    }
  }

  public void join() {
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
