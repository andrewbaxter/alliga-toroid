package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class MortarDataBinding implements Binding {
  public final JavaBytecodeBindingKey key;
  public final MortarDataTypestate type;
  public final JavaBytecodeCatchKey javaBytecodeCatchKey;

  public MortarDataBinding(JavaBytecodeBindingKey key, MortarDataTypestate type, JavaBytecodeCatchKey javaBytecodeCatchKey) {
    this.key = key;
    this.type = type;
    this.javaBytecodeCatchKey = javaBytecodeCatchKey;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(type.typestate_loadBinding(this));
  }

  @Override
  public Binding fork() {
  return new MortarDataBinding(key, type.typestate_fork(), javaBytecodeCatchKey);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return new MortarTargetCode(type.typestate_varBindDrop(context, location, this));
  }
}
