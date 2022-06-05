package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.rendaw.common.ROList;

/** Serializes non-shared fully typed values in semiserial graph (primitives, collections) */
public interface InlineType {
  Object desemiserializeValue(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      TypeInfo type,
      SemiserialSubvalue data);

  SemiserialSubvalue semiserializeValue(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Object> path,
      ROList<String> accessPath,
      TypeInfo type,
      Object value);
}
