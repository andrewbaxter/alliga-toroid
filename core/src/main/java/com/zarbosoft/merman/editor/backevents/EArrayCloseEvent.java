package com.zarbosoft.merman.editor.backevents;

import com.zarbosoft.pidgoon.events.MatchingEvent;

public class EArrayCloseEvent implements BackEvent {

  @Override
  public boolean matches(final MatchingEvent event) {
    return event instanceof EArrayCloseEvent;
  }

  @Override
  public String toString() {
    return String.format("ARRAY CLOSE");
  }
}
