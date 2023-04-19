package com.zarbosoft.alligatoroid.compiler.mortar;

public interface MortarRecordFieldable {
  /** Represents transfer, so no fork (or explicitly forked afterwards) */
  MortarRecordFieldstate asTupleFieldstate(int offset);
}
