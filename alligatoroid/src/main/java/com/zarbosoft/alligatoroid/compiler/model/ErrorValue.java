package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public final class ErrorValue implements Value {
  public static final ErrorValue error = new ErrorValue();

  private ErrorValue() {}

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return EvaluateResult.error;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, ErrorBinding.binding);
  }

  @Override
  public Location location() {
    return null;
  }

  @Override
  public Value type() {
    throw new Assertion();
  }

  @Override
  public boolean canExport() {
    return false;
  }

  @Override
  public SemiserialSubvalue graphSerialize(ImportId spec, Semiserializer semiserializer, ROList<Value> path, ROList<String> accessPath) {
    throw new Assertion();
  }

  @Override
  public Value graphDeserializeValue(ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data) {
    throw new Assertion();
  }
}
