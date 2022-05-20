package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportableType;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinExportableType
    implements IdentityExportableType {
  private final Constructor constructor;

  @Override
  public ExportableType exportableType() {
  return SingletonBuiltinExportableType.exportableType;
  }

  public AutoBuiltinExportableType(Class klass) {
    constructor = uncheck(() -> klass.getConstructors()[0]);
  }

  @Override
  public SemiserialSubvalue graphSemiserializeBody(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Object> path,
      ROList<String> accessPath,
      Object value) {
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
  public IdentityExportable graphDesemiserializeBody(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    Class klass = constructor.getDeclaringClass();
    IdentityExportable out =
        (IdentityExportable) uncheck(() -> klass.getConstructors()[0].newInstance());
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Object handleRecord(SemiserialRecord s) {
                  for (Field field : klass.getFields()) {
                    if (field.getAnnotation(Param.class) == null) continue;
                    uncheck(
                        () ->
                            field.set(
                                out,
                                AutoGraphUtils.autoDesemiAnyViaReflect(
                                    context,
                                    TypeInfo.fromField(field),
                                    s.data.get(SemiserialString.create(field.getName())))));
                  }
                  return null;
                }
              });
        });
    typeDesemiserializer.exportables.add(out);
    return out;
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface Param {}
}
