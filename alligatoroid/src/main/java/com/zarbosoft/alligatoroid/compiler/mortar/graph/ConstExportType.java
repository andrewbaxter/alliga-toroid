package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifactType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;

public class ConstExportType implements IdentityArtifactType, SingletonBuiltinArtifact {
  public static final ConstExportType exportType = new ConstExportType();
  public static final String KEY_TYPE = "type";
  public static final String KEY_VALUE = "value";

  @Override
  public IdentityArtifact graphDesemiserializeBody(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    final ConstDataValue out = new ConstDataValue();
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Artifact handleRecord(SemiserialRecord s) {
                  out.type = (MortarDataType) context.lookupRef((SemiserialSubvalueExportable) s.data.get(SemiserialString.create(KEY_TYPE)));
                  out.value = out.type.graphDesemiserializeValue(context, s.data.get(SemiserialString.create(KEY_VALUE)));
                  return null;
                }
              });
        });
    return out;
  }
}
