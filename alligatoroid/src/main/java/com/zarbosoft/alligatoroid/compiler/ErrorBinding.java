package com.zarbosoft.alligatoroid.compiler;

public class ErrorBinding implements Binding {
  public static final ErrorBinding binding = new ErrorBinding();

  private ErrorBinding() {}

  @Override
  public EvaluateResult fork(Context context, Location location) {
    return EvaluateResult.error;
  }

  @Override
  public TargetCode drop(Context context, Location location) {
    return null;
  }
}
