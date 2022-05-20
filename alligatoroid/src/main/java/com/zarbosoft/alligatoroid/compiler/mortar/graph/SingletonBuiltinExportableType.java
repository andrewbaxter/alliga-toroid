package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.rendaw.common.ROList;

public class SingletonBuiltinExportableType implements ExportableType {
  public final static SingletonBuiltinExportableType exportableType = new SingletonBuiltinExportableType();

  private SingletonBuiltinExportableType() {}

  @Override
  public SemiserialSubvalue graphSemiserializeValue(
          long importCacheId,
          Semiserializer semiserializer,
          ROList<Object> path,
          ROList<String> accessPath,
          Object value) {
    return SemiserialSubvalueRefBuiltin.create(Meta.builtinToSemiKey.get(value));
  }
}
