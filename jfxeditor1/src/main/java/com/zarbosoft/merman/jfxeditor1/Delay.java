package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import javafx.application.Platform;

public class Delay {
  public final Runnable inner;
  public final int delayMs;
  private Environment.HandleDelay handle;

  public Delay(int delayMs, Runnable inner) {
    this.inner = inner;
    this.delayMs = delayMs;
  }

  public void trigger(Context context) {
    synchronized (this) {
      if (handle != null) {
        handle.cancel();
      }
      handle =
          context.env.delay(
              500,
              () -> {
                synchronized (this) {
                  handle = null;
                }
                Platform.runLater(inner);
              });
    }
  }
}
