package com.zarbosoft.alligatoroid.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Threads {
  private final WeakHashMap<Thread, Object> threads = new WeakHashMap<>();

  public void join() {
    uncheck(
        () -> {
          List<Thread> joinThreads = new ArrayList<>();
          while (true) {
            joinThreads.clear();
            synchronized (threads) {
              joinThreads.addAll(threads.keySet());
              threads.clear();
            }
            if (joinThreads.isEmpty()) break;
            for (Thread thread : joinThreads) {
              thread.join();
            }
          }
        });
  }

  public void fork(Runnable r) {
    final Thread thread =
        new Thread(
            () -> {
              try {
                r.run();
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
    thread.setDaemon(true);
    thread.start();
    synchronized (this) {
      threads.put(thread, null);
    }
  }
}
