package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.Pregen.graphAuxConverters;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinExportableType implements RootExportable {
  private final Constructor constructor;

  public AutoBuiltinExportableType(Class klass) {
    constructor = uncheck(() -> klass.getConstructors()[0]);
  }

  private static String fieldName(Class klass, Parameter parameter) {
    if (LanguageElement.class.isAssignableFrom(klass) && parameter.getName().equals("id"))
      return "location";
    else return parameter.getName();
  }

  private SemiserialSubvalue prepNonCollectionArg(
      Semiserializer semiserializer,
      ImportId spec,
      Object data,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    Class t = data.getClass();
    InlineExportableType inlineExportableType;
    if ((inlineExportableType = graphAuxConverters.getOpt(t)) != null) {
      return inlineExportableType.semiserialize(data);
    } else if (Exportable.class.isAssignableFrom(t)) {
      return semiserializer.process(spec, (Exportable) data, path, accessPath);
    } else {
      semiserializer.errors.add(new UnexportablePre(accessPath));
      return null;
    }
  }

  // Needs to match AutoBuiltinValueType desemiserialize
  @Override
  public SemiserialSubvalue graphSemiserializeChild(
      Exportable child,
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    final Class<? extends Exportable> childClass = child.getClass();
    final Constructor<?> constructor = childClass.getConstructors()[0];
    final int paramCount = constructor.getParameterCount();
    TSList<SemiserialSubvalue> rootData = new TSList<>();
    for (int i = 0; i < paramCount; i++) {
      final Parameter parameter = constructor.getParameters()[i];
      final String fieldName = fieldName(childClass, parameter);
      Field field;
      try {
        field = childClass.getField(fieldName);
      } catch (NoSuchFieldException e) {
        throw Assertion.format(
            "%s param %s -> field %s doesn't exist",
            childClass.getName(), parameter.getName(), fieldName);
      }
      final Class<?> t = parameter.getType();
      final Object data = uncheck(() -> field.get(child));
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
              childClass.getCanonicalName(), field.getName());
        }
        rootData.add(prepNonCollectionArg(semiserializer, spec, data, path, paramAccessPath));
      }
    }
    return new SemiserialTuple(rootData);
  }

  public Object prepNonCollectionArg(Class t, SemiserialSubvalue data) {
    InlineExportableType inlineExportableType;
    if ((inlineExportableType = graphAuxConverters.getOpt(t)) != null) {
      return inlineExportableType.desemiserialize(data);
    } else return null;
  }

  @Override
  public Exportable graphDesemiserializeChild(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    TSList<ROPair<Field, SemiserialRefArtifact>> valueFields = new TSList<>();
    TSList<ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact>> valueListFields = new TSList<>();
    final int paramCount = constructor.getParameterCount();
    Object[] args = new Object[paramCount];
    data.dispatch(
        new SemiserialSubvalue.DefaultDispatcher<>() {
          @Override
          public Object handleTuple(SemiserialTuple s) {
            if (s.values.size() != paramCount)
              throw new RuntimeException(
                  "semiserial root tuple element count doesn't match constructor param count");
            for (int i = 0; i < paramCount; i++) {
              final SemiserialSubvalue subdata = s.values.get(i);
              final Parameter parameter = constructor.getParameters()[i];
              final Class<?> t = parameter.getType();
              Object primitiveArg = prepNonCollectionArg(t, subdata);
              if (primitiveArg != null) {
                args[i] = primitiveArg;
              } else if (Exportable.class.isAssignableFrom(t) || t == Object.class) {
                // Field maybe not exportable, but for the value to have been serialized it must
                // have been exportable
                args[i] =
                    subdata.dispatch(
                        new SemiserialSubvalue.DefaultDispatcher<>() {
                          @Override
                          public Object handleRef(SemiserialRef s) {
                            return s.dispatchRef(
                                new SemiserialRef.Dispatcher<Object>() {
                                  @Override
                                  public Object handleArtifact(SemiserialRefArtifact s) {
                                    Exportable found = context.artifactLookup.getOpt(s.id);
                                    if (found != null) return found;
                                    valueFields.add(
                                        new ROPair<>(
                                            uncheck(
                                                () ->
                                                    constructor
                                                        .getDeclaringClass()
                                                        .getField(
                                                            fieldName(
                                                                constructor.getDeclaringClass(),
                                                                parameter))),
                                            s));
                                    return null;
                                  }

                                  @Override
                                  public Object handleBuiltin(SemiserialRefBuiltin s) {
                                    return Builtin.semiKeyToBuiltin.get(s.key);
                                  }
                                });
                          }
                        });
              } else if (ROList.class.isAssignableFrom(t)) {
                args[i] =
                    subdata.dispatch(
                        new SemiserialSubvalue.DefaultDispatcher<>() {
                          @Override
                          public Object handleTuple(SemiserialTuple s) {
                            TSList out = new TSList();
                            for (SemiserialSubvalue subdata : s.values) {
                              final Class t1 =
                                  (Class)
                                      ((ParameterizedType) parameter.getParameterizedType())
                                          .getActualTypeArguments()[0];
                              final Object primitiveArg = prepNonCollectionArg(t1, subdata);
                              if (primitiveArg == null) {
                                if (Exportable.class.isAssignableFrom(t1)) {
                                  valueListFields.add(
                                      new ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact>(
                                          new ROPair<TSList, Integer>(out, out.size()),
                                          /** Must be artifact if prep func returns null * */
                                          (SemiserialRefArtifact) subdata));
                                } else throw new Assertion();
                              }
                              out.add(primitiveArg);
                            }
                            return out;
                          }
                        });
              } else {
                throw new Assertion();
              }
            }
            return null;
          }
        });
    Exportable out = (Exportable) uncheck(() -> constructor.newInstance(args));
    typeDesemiserializer.finishTasks.add(
        () -> {
          for (ROPair<Field, SemiserialRefArtifact> pair : valueFields) {
            uncheck(() -> pair.first.set(out, context.artifactLookup.get(pair.second.id)));
          }
          for (ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact> pair : valueListFields) {
            pair.first.first.set(pair.first.second, context.artifactLookup.get(pair.second.id));
          }
        });
    typeDesemiserializer.exportables.add(out);
    return out;
  }

  public interface InlineExportableType {
    Object desemiserialize(SemiserialSubvalue data);

    SemiserialSubvalue semiserialize(Object data);
  }
}
