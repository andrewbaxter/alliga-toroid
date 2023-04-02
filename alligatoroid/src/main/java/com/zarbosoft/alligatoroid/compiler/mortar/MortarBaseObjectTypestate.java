package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public abstract class MortarBaseObjectTypestate implements MortarDataTypestate {
  protected MortarBaseObjectTypestate() {}

  @Override
  public JavaBytecode typestate_arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadObj;
  }

  @Override
  public JavaBytecode typestate_arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreObj;
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  @Override
  public JavaBytecode typestate_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public EvaluateResult typestate_vary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(
        typestate_stackAsValue(((MortarTargetModuleContext) context.target).transfer((Exportable) data)));
  }
}
