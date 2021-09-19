package com.zarbosoft.atstdlib;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sync {
  private final Set<Class> seen = new HashSet<Class>();
  ExecutorService executor = Executors.newFixedThreadPool(64);
  private int outstanding = 0;

  public synchronized void push(GenerateClass k) {
    if (seen.contains(k.klass)) return;
    seen.add(k.klass);
    outstanding += 1;
    executor.submit(
        () -> {
          k.generate();
          done();
        });
  }

  private synchronized void done() {
    outstanding -= 1;
    if (outstanding == 0) {
      executor.shutdown();
    }
  }
}
