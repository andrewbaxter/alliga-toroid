package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueExportableBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.rendaw.common.ROList;

/** A single instance exists, exportable looks it up by class name turned into a key. */
public interface SingletonBuiltinArtifact extends Artifact {
  @Override
  default SemiserialSubvalueExportable graphSemiserialize(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath) {
    return SemiserialSubvalueExportableBuiltin.create(Meta.builtinToSemiKey.get(this));
  }
}
