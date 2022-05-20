package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;

public class ConstExportType implements IdentityExportableType, SingletonBuiltinExportable {
  public static final ConstExportType exportType = new ConstExportType();
  public static final String KEY_TYPE = "type";
  public static final String KEY_VALUE = "value";

  @Override
  public IdentityExportable graphDesemiserializeBody(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    final ConstDataValue out = new ConstDataValue();
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Exportable handleRecord(SemiserialRecord s) {
                  out.type = (MortarDataType) context.lookupRef((SemiserialSubvalueRef) s.data.get(SemiserialString.create(KEY_TYPE)));
                  out.value = out.type.graphDesemiserializeValue(context, s.data.get(SemiserialString.create(KEY_VALUE)));
                  return null;
                }
              });
        });
    return out;
  }
}
