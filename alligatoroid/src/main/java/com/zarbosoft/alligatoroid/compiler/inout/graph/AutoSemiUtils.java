package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public class AutoSemiUtils {
  public static SemiserialUnknown autoSemiAny(
      Semiserializer semiserializer,
      long importCacheId,
      Object data,
      ROList<Object> path,
      ROList<String> accessPath) {
    ExportableType type = null;
    if (data instanceof Exportable) {
      type = ((Exportable) data).exportableType();
    }
    if (type == null) {
      type = Meta.detachedExportableTypeLookup.getOpt(data.getClass());
    }
    if (type == null) {
      throw new Assertion();
    }
    return type.semiserializeValue(importCacheId, semiserializer, path, accessPath, data);
  }

  public static SemiserialSubvalue autoSemiAnyViaReflect(
      Semiserializer semiserializer,
      long importCacheId,
      TypeInfo type,
      Object data,
      ROList<Object> path,
      ROList<String> accessPath) {
    InlineType inliner = Meta.inlineTypeLookup.get(data.getClass());
    if (inliner != null) {
      return inliner.semiserializeValue(
          importCacheId, semiserializer, path, accessPath, type, data);
    }
    return autoSemiAny(semiserializer, importCacheId, data, path, accessPath);
  }

  public static Object autoDesemiAnyViaReflect(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      TypeInfo type,
      SemiserialSubvalue data) {
    InlineType inliner = Meta.inlineTypeLookup.get(type.klass);
    if (inliner != null) {
      return inliner.desemiserializeValue(context, typeDesemiserializer, type, data);
    }
    return autoDesemiAny(
        context,
        data.dispatch(
            new SemiserialSubvalue.DefaultDispatcher<SemiserialUnknown>() {
              @Override
              public SemiserialUnknown handleUnknown(SemiserialUnknown s) {
                return s;
              }
            }));
  }

  private static Object autoDesemiAny(ModuleCompileContext context, SemiserialUnknown data) {
    return context.lookupRef(data);
  }
}
