package com.zarbosoft.merman.core.syntax.primitivepattern;

import com.zarbosoft.pidgoon.events.MatchingEvent;

public class CharacterEvent implements MatchingEvent {
  public final String value;

  public CharacterEvent(final String value) {
    this.value = value;
  }

  @Override
  public boolean matches(final MatchingEvent event) {
    return value.equals(((CharacterEvent) event).value);
  }
}
