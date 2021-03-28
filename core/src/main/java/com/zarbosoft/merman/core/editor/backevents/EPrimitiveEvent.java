package com.zarbosoft.merman.core.editor.backevents;

import com.zarbosoft.pidgoon.events.MatchingEvent;
import com.zarbosoft.rendaw.common.Format;

public class EPrimitiveEvent implements BackEvent {
  public String value = null;

  public EPrimitiveEvent(final String value) {
    this.value = value;
  }

  public EPrimitiveEvent() {}

  @Override
  public boolean matches(final MatchingEvent event) {
    if (value == null) return event instanceof EPrimitiveEvent;
    else return event instanceof EPrimitiveEvent && value.equals(((EPrimitiveEvent) event).value);
  }

  @Override
  public String toString() {
    return Format.format("PRIMITIVE %s", value == null ? "*" : "\""+value+"\"");
  }
}
