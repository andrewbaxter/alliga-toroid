package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.Pregen.graphAuxConverters;
import static com.zarbosoft.rendaw.common.Common.uncheck;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface AutoBuiltinExportable extends Exportable {
  @Override
  default Exportable type() {
    return Builtin.autoBuiltinTypes.get(getClass());
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
    AutoBuiltinExportableType.GraphAuxConverter graphAuxConverter;
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
      final String fieldName;
      if (LanguageElement.class.isAssignableFrom(getClass()) && parameter.getName().equals("id"))
        fieldName = "location";
      else fieldName = parameter.getName();
      Field field;
      try {
        field = getClass().getField(fieldName);
      } catch (NoSuchFieldException e) {
        throw Assertion.format(
            "%s param %s -> field %s doesn't exist",
            getClass().getName(), parameter.getName(), fieldName);
      }
      final Class<?> t = parameter.getType();
      final Object data = uncheck(() -> field.get(this));
      final TSList<String> paramAccessPath = accessPath.mut().add(fieldName);
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
