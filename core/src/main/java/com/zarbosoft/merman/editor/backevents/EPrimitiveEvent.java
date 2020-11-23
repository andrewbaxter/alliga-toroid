package com.zarbosoft.merman.editor.backevents;

import com.zarbosoft.pidgoon.events.MatchingEvent;

public class EPrimitiveEvent implements BackEvent {
  public String value = null;

  public EPrimitiveEvent(final String value) {
    this.value = value;
  }

  public EPrimitiveEvent() {}

  @Override
  public boolean matches(final MatchingEvent event) {
    if (value == null) return event instanceof EPrimitiveEvent || event instanceof JNullEvent;
    else return event instanceof EPrimitiveEvent && value.equals(((EPrimitiveEvent) event).value);
  }

  @Override
  public String toString() {
    if (value == null) return "*";
    return String.format("\"%s\"", value);
  }
}
