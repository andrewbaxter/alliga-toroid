package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.OkValue;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class JVMValue implements OkValue {
  public final JVMProtocode lower;
  private final JVMDataType type;

  public JVMValue(JVMDataType type, JVMProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
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
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.valueBind(context.moduleContext, lower);
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
