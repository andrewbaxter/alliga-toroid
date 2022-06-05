package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public class BuiltinSingletonExportableType implements ExportableType {
  public final static BuiltinSingletonExportableType exportableType = new BuiltinSingletonExportableType();

  private BuiltinSingletonExportableType() {}

  @Override
  public Object graphDesemiserializeBody(ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data) {
    throw new Assertion();
  }

  @Override
  public SemiserialSubvalue graphSemiserializeBody(long importCacheId, Semiserializer semiserializer, ROList<Object> path, ROList<String> accessPath, Object value) {
    return SemiserialBuiltinRef.create(Meta.singletonExportableKeyLookup.get(value));
  }

  @Override
  public ExportableType exportableType() {
    throw new Assertion();
  }
}
