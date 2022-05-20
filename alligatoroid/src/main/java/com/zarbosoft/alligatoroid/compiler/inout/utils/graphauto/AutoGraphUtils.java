package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.error.UnexportablePre;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.util.Map;

public class AutoGraphUtils {
  public static SemiserialSubvalue semiSubAnyViaReflect(
      Semiserializer semiserializer,
      long importCacheId,
      TypeInfo type,
      Object data,
      ROList<Object> path,
      ROList<String> accessPath) {
    ExportableType primitiveExportType;
    if ((primitiveExportType = Meta.builtinExportTypes.getOpt(type.klass)) != null) {
      return primitiveExportType.graphSemiserializeValue(
          importCacheId, semiserializer, path, accessPath, data);
    } else if (ROList.class.isAssignableFrom(type.klass)) {
      TSList<SemiserialSubvalue> elementData = new TSList<>();
      for (int i = 0; i < ((ROList) data).size(); i++) {
        final Object el = ((ROList) data).get(i);
        SemiserialSubvalue subNonCollectionArg =
            AutoGraphUtils.semiSubAnyViaReflect(
                semiserializer,
                importCacheId,
                type.genericArgs[0],
                el,
                path,
                accessPath.mut().add(Integer.toString(i)));
        if (subNonCollectionArg != null) {
          elementData.add(subNonCollectionArg);
        } else throw new Assertion();
      }
      return SemiserialTuple.create(elementData);
    } else if (ROMap.class.isAssignableFrom(type.klass)) {
      TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> elementData = new TSOrderedMap<>();
      for (Object el0 : ((ROMap) data)) {
        Map.Entry el = (Map.Entry) el0;
        final SemiserialSubvalue k =
            semiSubAnyViaReflect(
                semiserializer, importCacheId, type.genericArgs[0], el.getKey(), path, accessPath);
        final SemiserialSubvalue v =
            semiSubAnyViaReflect(
                semiserializer,
                importCacheId,
                type.genericArgs[1],
                el.getValue(),
                path,
                accessPath);
        if (k == null || v == null) throw new Assertion();
        elementData.putNew(k, v);
      }
      return SemiserialRecord.create(elementData);
    } else {
      semiserializer.errors.add(new UnexportablePre(accessPath));
      return null;
    }
  }

  public static Object autoDesemiAnyViaReflect(
      ModuleCompileContext context, TypeInfo type, SemiserialSubvalue data) {
    ROPair<Boolean, Object> desemiRef =
        data.dispatch(
            new SemiserialSubvalue.DefaultDispatcher<>() {
              @Override
              public ROPair<Boolean, Object> handleDefault(SemiserialSubvalue s) {
                return new ROPair<>(false, null);
              }

              @Override
              public ROPair<Boolean, Object> handleRef(SemiserialSubvalueRef s) {
                return new ROPair<>(true, context.lookupRef(s));
              }
            });
    if (desemiRef.first) return desemiRef.second;
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
            public Object handleRef(SemiserialSubvalueRef s) {
              return context.lookupRef(s);
            }
          });
    } else if (ROList.class.isAssignableFrom(type.klass)) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Object handleTuple(SemiserialTuple s) {
              TSList out = new TSList();
              for (SemiserialSubvalue subdata : s.values) {
                out.add(autoDesemiAnyViaReflect(context, type.genericArgs[0], subdata));
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
                    autoDesemiAnyViaReflect(context, type.genericArgs[0], datum.first),
                    autoDesemiAnyViaReflect(context, type.genericArgs[1], datum.second));
              }
              return out;
            }
          });
    } else {
      throw new Assertion();
    }
  }
}
