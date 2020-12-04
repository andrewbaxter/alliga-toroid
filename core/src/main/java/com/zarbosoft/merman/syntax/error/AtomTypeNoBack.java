package com.zarbosoft.merman.syntax.error;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AtomTypeNoBack extends BaseKVError{
  public AtomTypeNoBack() {
    super(ImmutableMap.of());
  }

  @Override
  protected String name() {
    return "atom type, no back";
  }
}
