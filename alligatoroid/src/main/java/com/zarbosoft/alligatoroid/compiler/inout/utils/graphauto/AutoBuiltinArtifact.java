package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifactType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.rendaw.common.Common.uncheck;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface AutoBuiltinArtifact extends IdentityArtifact {
  @Override
  default IdentityArtifactType exportableType() {
    return Meta.autoBuiltinExportTypes.get(getClass());
  }

  @Override
  default SemiserialSubvalue graphSemiserializeBody(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath) {
    TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> rootData = new TSOrderedMap<>();
    for (Field field : getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) continue;
      if (Modifier.isFinal(field.getModifiers())) {
        throw Assertion.format(
            "FIX! Builtin %s field %s is exportable but final",
            getClass().getCanonicalName(), field.getName());
      }
      final TSList<String> paramAccessPath = accessPath.mut().add(field.getName());
      rootData.putNew(
          SemiserialString.create(field.getName()),
          AutoGraphUtils.semiSubAnyViaReflect(
              semiserializer,
              importCacheId,
              TypeInfo.fromField(field),
              uncheck(() -> field.get(this)),
              path,
              paramAccessPath));
    }
    return SemiserialRecord.create(rootData);
  }

  @Override
  default void postInit() {}
}
