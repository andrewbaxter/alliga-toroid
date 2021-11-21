package com.zarbosoft.merman.core.syntax.primitivepattern;

import com.zarbosoft.pidgoon.events.Event;

public class CharacterEvent implements Event {
  public final String value;

  public CharacterEvent(final String value) {
    this.value = value;
  }
}
