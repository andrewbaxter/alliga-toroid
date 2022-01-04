package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.rendaw.common.ROList;

public interface GraphValue {
  /**
   * Presents the object structure as a hierarchy of simple values for cloning + caching.
   *
   * @return
   */
  SemiserialSubvalue graphSerialize(
      ImportId spec, Semiserializer semiserializer, ROList<Value> path, ROList<String> accessPath);

  /**
   * Takes records/tuples/values and turns them into a value with the type of this value (only
   * applicable to type values).
   *
   * @param context
   * @param typeDesemiserializer
   * @param data
   * @return
   */
  Value graphDeserializeValue(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data);

  /**
   * Called after deferred initialization in graph desemiserialization.
   *
   */
  void postDesemiserialize();
}
