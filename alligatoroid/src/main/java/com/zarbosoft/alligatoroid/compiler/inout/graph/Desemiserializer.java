package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.TSList;

public class Desemiserializer {
  public final TSList<Runnable> finishTasks = new TSList<>();
  public final TSList<Artifact> exportables = new TSList<>();

  public void finish() {
    for (Runnable task : finishTasks) {
      task.run();
    }
    for (Artifact artifact : exportables) {
      artifact.postInit();
    }
  }
}
