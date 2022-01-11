package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.TypeDependencyLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Semiserializer {
  public final TSList<SemiserialValue> artifacts = new TSList<>();
  public final TSMap<Value, ArtifactId> artifactLookup;

  public Semiserializer(TSMap<Value, ArtifactId> artifactLookup) {
    this.artifactLookup = artifactLookup;
  }

  public SemiserialRef process(
      ImportId spec, Value value, ROList<Value> path, ROList<String> accessPath) {
    if (!value.canExport()) {
      throw new UnexportablePre(accessPath);
    }
    if (path.contains(value)) {
      throw new TypeDependencyLoopPre();
    }
    {
      String found = Builtin.builtinToSemiKey.getOpt(value);
      if (found != null) {
        return new SemiserialRefBuiltin(found);
      }
    }
    {
      ArtifactId found = artifactLookup.getOpt(value);
      if (found != null) {
        return new SemiserialRefArtifact(found);
      }
    }
    int index = artifacts.size();
    artifacts.add(null);
    final ArtifactId id = new ArtifactId(spec, index);
    artifactLookup.put(value, id);
    final TSList<Value> newPath = path.mut().add(value);
    SemiserialSubvalue data = value.graphSerialize(spec, this, newPath, accessPath);
    artifacts.set(
        index,
        new SemiserialValue(
            process(spec, value.type(), newPath, accessPath.mut().add("(type)")), data));
    return new SemiserialRefArtifact(id);
  }
}
