package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.Pregen.graphAuxConverters;
import static com.zarbosoft.rendaw.common.Common.uncheck;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface AutoExportable extends Exportable {
  public static void assertFieldsOk(Class klass) {
    final Constructor constructor = klass.getConstructors()[0];
    for (Parameter parameter : constructor.getParameters()) {
      final Field field = uncheck(() -> klass.getField(parameter.getName()));
      if (Modifier.isFinal(field.getModifiers()))
        throw new RuntimeException(
            Format.format(
                "%s param %s -> field %s is final",
                klass.getName(), parameter.getName(), field.getName()));
      if (Modifier.isPrivate(field.getModifiers()))
        throw new RuntimeException(
            Format.format(
                "%s param %s -> field %s is private",
                klass.getName(), parameter.getName(), field.getName()));
    }
  }

  @Override
  default Exportable type() {
    return Builtin.builtinToBuiltinType.get(getClass());
  }

  @Override
  default void postDesemiserialize() {}

  default SemiserialSubvalue prepNonCollectionArg(
      Semiserializer semiserializer,
      ImportId spec,
      Object data,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    Class t = data.getClass();
    AutoExportableType.GraphAuxConverter graphAuxConverter;
    if ((graphAuxConverter = graphAuxConverters.getOpt(t)) != null) {
      return graphAuxConverter.semiserialize(data);
    } else if (Exportable.class.isAssignableFrom(t)) {
      return semiserializer.process(spec, (Exportable) data, path, accessPath);
    } else {
      semiserializer.errors.add(new UnexportablePre(accessPath));
      return null;
    }
  }

  // Needs to match AutoBuiltinValueType desemiserialize
  @Override
  default SemiserialSubvalue graphSemiserialize(
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    final Constructor<?> constructor = getClass().getConstructors()[0];
    final int paramCount = constructor.getParameterCount();
    TSList<SemiserialSubvalue> rootData = new TSList<>();
    for (int i = 0; i < paramCount; i++) {
      final Parameter parameter = constructor.getParameters()[i];
      Field field = uncheck(() -> getClass().getField(parameter.getName()));
      if (field == null) {
        throw Assertion.format(
            "No field matching parameter %s in %s",
            parameter.getName(), getClass().getCanonicalName());
      }
      final Class<?> t = parameter.getType();
      final Object data = uncheck(() -> field.get(this));
      final TSList<String> paramAccessPath = accessPath.mut().add(parameter.getName());
      if (ROList.class.isAssignableFrom(t)) {
        TSList<SemiserialSubvalue> elementData = new TSList<>();
        for (int collI = 0; collI < ((ROList) data).size(); collI++) {
          final Object el = ((ROList) data).get(collI);
          SemiserialSubvalue subNonCollectionArg =
              prepNonCollectionArg(
                  semiserializer,
                  spec,
                  el,
                  path,
                  paramAccessPath.mut().add(Integer.toString(collI)));
          if (subNonCollectionArg != null) {
            elementData.add(subNonCollectionArg);
          } else throw new Assertion();
        }
        rootData.add(new SemiserialTuple(elementData));
      } else {
        if (data instanceof Exportable && Modifier.isFinal(field.getModifiers())) {
          throw Assertion.format(
              "FIX! Builtin %s field %s is exportable but final",
              getClass().getCanonicalName(), field.getName());
        }
        rootData.add(prepNonCollectionArg(semiserializer, spec, data, path, paramAccessPath));
      }
    }
    return new SemiserialTuple(rootData);
  }
}
