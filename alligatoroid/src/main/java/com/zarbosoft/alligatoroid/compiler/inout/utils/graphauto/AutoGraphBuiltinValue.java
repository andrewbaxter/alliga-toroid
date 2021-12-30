package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.BuiltinValue;
import com.zarbosoft.alligatoroid.compiler.model.Value;
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
public interface AutoGraphBuiltinValue extends BuiltinValue {
  SemiserialString TYPE_KEY = new SemiserialString("type");

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
  default boolean canExport() {
    return true;
  }

  default SemiserialSubvalue prepNonCollectionArg(
      Semiserializer semiserializer,
      ImportId spec,
      Object data,
      ROList<Value> path,
      ROList<String> accessPath) {
    Class t = data.getClass();
    AutoGraphBuiltinValueType.GraphAuxConverter graphAuxConverter;
    if ((graphAuxConverter = graphAuxConverters.getOpt(t)) != null) {
      return graphAuxConverter.semiserialize(data);
    } else if (Value.class.isAssignableFrom(t)) {
      return semiserializer.process(spec, (Value) data, path, accessPath);
    } else return null;
  }

  // Needs to match AutoBuiltinValueType desemiserialize
  @Override
  default SemiserialSubvalue graphSerialize(
      ImportId spec, Semiserializer semiserializer, ROList<Value> path, ROList<String> accessPath) {
    final Constructor<?> constructor = getClass().getConstructors()[0];
    final int paramCount = constructor.getParameterCount();
    TSList<SemiserialSubvalue> rootData = new TSList<>();
    for (int i = 0; i < paramCount; i++) {
      final Parameter parameter = constructor.getParameters()[i];
      Field field = uncheck(() -> getClass().getField(parameter.getName()));
      final Class<?> t = parameter.getType();
      final Object data = uncheck(() -> field.get(this));
      final TSList<String> paramAccessPath = accessPath.mut().add(parameter.getName());
      SemiserialSubvalue nonCollectionArg =
          prepNonCollectionArg(semiserializer, spec, data, path, paramAccessPath);
      if (nonCollectionArg != null) {
        rootData.add(nonCollectionArg);
      } else if (ROList.class.isAssignableFrom(t)) {
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
        throw new Assertion();
      }
    }
    return new SemiserialTuple(rootData);
  }
}
