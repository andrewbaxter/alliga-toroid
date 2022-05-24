package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;

/**
 * An identity exportable provides a flattening point during semiserialization. A referenced identity exportable will be
 * serialized as a reference in the referrer, and then added to the top level array of artifacts. Multiple referrers will
 * refer to the same artifact (it only gets semiserialized once)
 */
public interface IdentityArtifactType extends Artifact {
  IdentityArtifact graphDesemiserializeBody(
      ModuleCompileContext context, Desemiserializer typeDesemiserializer, SemiserialSubvalue data);
}
