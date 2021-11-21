package com.zarbosoft.pidgoon.errors;

public class AbortParseAt extends RuntimeException {
  public final Object at;
  public final AbortParse e;

  public AbortParseAt(Object at, AbortParse e) {
    this.at = at;
    this.e = e;
  }
}
