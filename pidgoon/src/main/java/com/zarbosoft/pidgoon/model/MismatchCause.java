package com.zarbosoft.pidgoon.model;

public class MismatchCause {
  public final Node node;

  public MismatchCause(Node node) {
    this.node = node;
  }

  @Override
  public String toString() {
    StringBuilder message = new StringBuilder();
    message.append(node.toString());
    return message.toString();
  }
}
