package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoGraphUtils;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.PrimitiveExportType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataStackValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class ConstExportType implements IdentityExportableType, SingletonBuiltinExportable {
  public static final ConstExportType exportType = new ConstExportType();
  private static final String KEY_TYPE = "type";
  private static final String KEY_VALUE = "value";

  @Override
  public SemiserialSubvalue graphSemiserializeArtifact(
      Object child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    final ConstDataValue const_ = (ConstDataValue) child;
    final Object inner = const_.getInner();
    final TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data =
        new TSOrderedMap<>(
            s ->
                s.putNew(
                    SemiserialString.create(KEY_TYPE),
                    const_
                        .mortarType()
                        .graphType()
                        .graphSemiserialize(
                            const_.mortarType(), spec, semiserializer, path, accessPath)));
    PrimitiveExportType primitiveType =
        Meta.primitiveMortarTypeToExportType.getOpt(const_.mortarType());
    if (primitiveType != null) {
      data.putNew(SemiserialString.create(KEY_VALUE), primitiveType.semiserialize(inner));
    } else {
      data.putNew(
          SemiserialString.create(KEY_VALUE),
          ((Exportable) inner)
              .graphType()
              .graphSemiserialize(inner, spec, semiserializer, path, accessPath));
    }
    return SemiserialRecord.create(data);
  }

  @Override
  public Object graphDesemiserializeArtifact(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    final ConstDataStackValue out = new ConstDataStackValue();
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Exportable handleRecord(SemiserialRecord s) {
                  out.type =
                      (MortarDataType)
                          AutoGraphUtils.autoDesemiAny(
                              context,
                              TypeInfo.fromClass(MortarDataType.class),
                              s.data.get(SemiserialString.create(KEY_TYPE)));
                  final SemiserialSubvalue value = s.data.get(SemiserialString.create(KEY_VALUE));
                  PrimitiveExportType primitiveType =
                      Meta.primitiveMortarTypeToExportType.getOpt(out.type);
                  if (primitiveType != null) {
                    out.value = primitiveType.desemiserialize(value);
                  } else {
                    out.value =
                        value.dispatch(
                            new SemiserialSubvalue.DefaultDispatcher<>() {
                              @Override
                              public Object handleRef(SemiserialRef s) {
                                return context.lookupRef(s);
                              }
                            });
                  }
                  return null;
                }
              });
        });
    return out;
  }
}
