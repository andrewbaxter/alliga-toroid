package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.TypeDependencyLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Semiserializer {
  public final TSList<Error.PreError> errors;
  public final TSList<SemiserialValue> artifacts = new TSList<>();
  public final TSMap<ObjId<Exportable>, ArtifactId> artifactLookup;

  public Semiserializer(
          TSList<Error.PreError> errors, TSMap<ObjId<Exportable>, ArtifactId> artifactLookup) {
    this.errors = errors;
    this.artifactLookup = artifactLookup;
  }

  public static SemiserialModule semiserialize(
          ModuleCompileContext moduleContext, MortarValue value, Location location) {
    TSList<Error.PreError> errors = new TSList<>();
    Semiserializer s = new Semiserializer(errors, moduleContext.backArtifactLookup);
    final SemiserialRef out =
        s.process(moduleContext.importId, value, new TSList<>(), new TSList<>());
    if (errors.some()) {
      for (Error.PreError error : errors) {
        moduleContext.errors.add(error.toError(location));
      }
      return null;
    }
    return new SemiserialModule(out, s.artifacts);
  }

  public SemiserialRef process(
      ImportId spec, Exportable value, ROList<Exportable> path, ROList<String> accessPath) {
    if (path.contains(value)) {
      errors.add(new TypeDependencyLoopPre(accessPath));
      return null;
    }
    {
      String found = Builtin.builtinToSemiKey.getOpt(value);
      if (found != null) {
        return new SemiserialRefBuiltin(found);
      }
    }
    {
      ArtifactId found = artifactLookup.getOpt(new ObjId<>(value));
      if (found != null) {
        return new SemiserialRefArtifact(found);
      }
    }
    int index = artifacts.size();
    artifacts.add(null);
    final ArtifactId id = new ArtifactId(spec, index);
    artifactLookup.put(new ObjId<>(value), id);
    final TSList<Exportable> newPath = path.mut().add(value);
    final Exportable exportableType = value.type();
    SemiserialSubvalue data =
        exportableType.graphSemiserializeChild(value, spec, this, newPath, accessPath);
    artifacts.set(
        index,
        new SemiserialValue(
            process(spec, exportableType, newPath, accessPath.mut().add("(type)")), data));
    return new SemiserialRefArtifact(id);
  }
}
