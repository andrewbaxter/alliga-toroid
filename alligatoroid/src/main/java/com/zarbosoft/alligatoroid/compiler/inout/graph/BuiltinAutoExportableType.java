package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoDesemiAnyViaReflect;
import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoSemiAnyViaReflect;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class BuiltinAutoExportableType implements ExportableType, BuiltinSingletonExportable {
  private final Constructor constructor;

  public BuiltinAutoExportableType(Class klass) {
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
          autoSemiAnyViaReflect(
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
  public Object graphDesemiserializeBody(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    Class klass = constructor.getDeclaringClass();
    Exportable out = (Exportable) uncheck(() -> klass.getConstructors()[0].newInstance());
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
                                autoDesemiAnyViaReflect(
                                    context,
                                    typeDesemiserializer,
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
