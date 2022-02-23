package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoGraphUtils {
  public static SemiserialSubvalue semiArtifact(
      Semiserializer semiserializer,
      ImportId spec,
      TypeInfo type,
      Object obj,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> rootData = new TSOrderedMap<>();
    for (Field field : type.klass.getFields()) {
      if (Modifier.isStatic(field.getModifiers())) continue;
      if (Modifier.isFinal(field.getModifiers())) {
        throw Assertion.format(
            "FIX! Builtin %s field %s is exportable but final",
            type.klass.getCanonicalName(), field.getName());
      }
      final TSList<String> paramAccessPath = accessPath.mut().add(field.getName());
      rootData.putNew(
          SemiserialString.create(field.getName()),
          AutoGraphUtils.semiSubAny(
              semiserializer,
              spec,
              TypeInfo.fromField(field),
              uncheck(() -> field.get(obj)),
              path,
              paramAccessPath));
    }
    return SemiserialRecord.create(rootData);
  }

  public static Exportable desemiArtifact(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      Class klass,
      SemiserialSubvalue data) {
    Exportable out = (Exportable) uncheck(() -> klass.getConstructors()[0].newInstance());
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Object handleRecord(SemiserialRecord s) {
                  for (Field field : klass.getFields()) {
                    if (field.getAnnotation(Exportable.Param.class) == null) continue;
                    uncheck(
                        () ->
                            field.set(
                                out,
                                AutoGraphUtils.autoDesemiAny(
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

  public static SemiserialSubvalue semiSubAny(
      Semiserializer semiserializer,
      ImportId spec,
      TypeInfo type,
      Object data,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    PrimitiveExportType primitiveExportType;
    if ((primitiveExportType = Meta.primitiveTypeToExportType.getOpt(type.klass)) != null) {
      return primitiveExportType.semiserialize(data);
    } else if (Exportable.class.isAssignableFrom(type.klass)) {
      if (data == null) return SemiserialString.create("null");
      return ((Exportable) data)
          .graphType()
          .graphSemiserialize(data, spec, semiserializer, path, accessPath);
    } else if (ROList.class.isAssignableFrom(type.klass)) {
      TSList<SemiserialSubvalue> elementData = new TSList<>();
      for (int i = 0; i < ((ROList) data).size(); i++) {
        final Object el = ((ROList) data).get(i);
        SemiserialSubvalue subNonCollectionArg =
            AutoGraphUtils.semiSubAny(
                semiserializer,
                spec,
                type.genericArgs[0],
                el,
                path,
                accessPath.mut().add(Integer.toString(i)));
        if (subNonCollectionArg != null) {
          elementData.add(subNonCollectionArg);
        } else throw new Assertion();
      }
      return SemiserialTuple.createSemiserialTuple(elementData);
    } else if (ROMap.class.isAssignableFrom(type.klass)) {
      TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> elementData = new TSOrderedMap<>();
      for (Object el0 : ((ROMap) data)) {
        Map.Entry el = (Map.Entry) el0;
        final SemiserialSubvalue k =
            semiSubAny(semiserializer, spec, type.genericArgs[0], el.getKey(), path, accessPath);
        final SemiserialSubvalue v =
            semiSubAny(semiserializer, spec, type.genericArgs[1], el.getValue(), path, accessPath);
        if (k == null || v == null) throw new Assertion();
        elementData.putNew(k, v);
      }
      return SemiserialRecord.create(elementData);
    } else {
      semiserializer.errors.add(new UnexportablePre(accessPath));
      return null;
    }
  }

  public static Object autoDesemiAny(
      ModuleCompileContext context, TypeInfo type, SemiserialSubvalue data) {
    PrimitiveExportType primitiveExportType;
    if ((primitiveExportType = Meta.primitiveTypeToExportType.getOpt(type.klass)) != null) {
      return primitiveExportType.desemiserialize(data);
    } else if (Exportable.class.isAssignableFrom(type.klass)) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Object handleString(SemiserialString s) {
              if (s.value.equals("null")) return null;
              else return super.handleString(s);
            }

            @Override
            public Exportable handleRef(SemiserialRef s) {
              return (Exportable) context.lookupRef(s);
            }
          });
    } else if (ROList.class.isAssignableFrom(type.klass)) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Object handleTuple(SemiserialTuple s) {
              TSList out = new TSList();
              for (SemiserialSubvalue subdata : s.values) {
                out.add(autoDesemiAny(context, type.genericArgs[0], subdata));
              }
              return out;
            }
          });
    } else if (ROMap.class.isAssignableFrom(type.klass)) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Object handleRecord(SemiserialRecord s) {
              TSMap out = new TSMap();
              for (ROPair<SemiserialSubvalue, SemiserialSubvalue> datum : s.data) {
                out.put(
                    autoDesemiAny(context, type.genericArgs[0], datum.first),
                    autoDesemiAny(context, type.genericArgs[1], datum.second));
              }
              return out;
            }
          });
    } else {
      throw new Assertion();
    }
  }
}
