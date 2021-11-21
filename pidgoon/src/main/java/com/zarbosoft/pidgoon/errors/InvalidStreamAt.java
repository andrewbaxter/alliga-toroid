package com.zarbosoft.pidgoon.errors;

public class InvalidStreamAt extends InvalidStream {
  public final Object at;

  public InvalidStreamAt(Object at, InvalidStream e) {
    super(e.step);
    this.at = at;
  }
}
