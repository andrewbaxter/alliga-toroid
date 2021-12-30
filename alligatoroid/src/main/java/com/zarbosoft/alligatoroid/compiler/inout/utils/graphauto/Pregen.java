package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialType;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.Meta.toUnderscore;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Pregen {
  public static final ROMap<Class, AutoGraphBuiltinValueType.GraphAuxConverter> graphAuxConverters;

  static {
    //// Graph id/primitive type conversions
    // =============================
    /// Prepare type converters for non-value, non-collection types
    TSMap<Class, AutoGraphBuiltinValueType.GraphAuxConverter> graphAuxConverters0 = new TSMap<>();

    /// Simple types
    AutoGraphBuiltinValueType.GraphAuxConverter intConverter =
        new AutoGraphBuiltinValueType.GraphAuxConverter() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<Object>() {
                  @Override
                  public Object handleInt(SemiserialInt s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialInt((Integer) data);
          }
        };
    graphAuxConverters0.put(Integer.class, intConverter);
    graphAuxConverters0.put(int.class, intConverter);
    AutoGraphBuiltinValueType.GraphAuxConverter boolConverter =
        new AutoGraphBuiltinValueType.GraphAuxConverter() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleBool(SemiserialBool s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialBool((Boolean) data);
          }
        };
    graphAuxConverters0.put(Boolean.class, boolConverter);
    graphAuxConverters0.put(boolean.class, boolConverter);
    AutoGraphBuiltinValueType.GraphAuxConverter stringConverter =
        new AutoGraphBuiltinValueType.GraphAuxConverter() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleString(SemiserialString s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialString((String) data);
          }
        };
    graphAuxConverters0.put(String.class, stringConverter);

    /// Complexer types
    // Walk type tree to find children of polymorphic types.
    TSMap<Class, TSSet<Class>> children = new TSMap<>();
    class WalkParents {
      void walk(Class klass, Class from) {
        if (from != null) {
          final TSSet<Class> childrenAt = children.getCreate(klass, () -> new TSSet<>());
          childrenAt.add(from);
        }
        walk(klass.getSuperclass(), klass);
        for (Class i : klass.getInterfaces()) {
          walk(i, klass);
        }
      }
    }
    WalkParents walk = new WalkParents();
    for (Class klass :
        new Class[] {
          ImportId.class,
          RemoteModuleId.class,
          LocalModuleId.class,
          BundleModuleSubId.class,
          Location.class,
        }) {
      walk.walk(klass, null);
    }

    // For each type that could appear, generate converters.
    // If it has children - treat it like an enum
    // If no children/concrete - serialize as record
    for (Map.Entry<Class, TSSet<Class>> e : children) {
      if (e.getValue().some()) {
        TSMap<String, Class> keys = new TSMap<>();
        TSMap<Class, String> backKeys = new TSMap<>();
        for (Class child : e.getValue()) {
          String key = toUnderscore(child);
          keys.put(key, child);
          backKeys.put(child, key);
        }
        graphAuxConverters0.put(
            e.getKey(),
            new AutoGraphBuiltinValueType.GraphAuxConverter() {
              @Override
              public Object desemiserialize(SemiserialSubvalue data) {
                return data.dispatch(
                    new SemiserialSubvalue.DefaultDispatcher<>() {
                      @Override
                      public Object handleType(SemiserialType s) {
                        return graphAuxConverters0.get(keys.get(s.type)).desemiserialize(s.value);
                      }
                    });
              }

              @Override
              public SemiserialSubvalue semiserialize(Object data) {
                final String key = backKeys.get(data.getClass());
                return new SemiserialType(
                    key, graphAuxConverters0.get(data.getClass()).semiserialize(data));
              }
            });
      } else {
        Class klass = e.getKey();
        Constructor constructor = klass.getConstructors()[0];
        graphAuxConverters0.put(
            klass,
            new AutoGraphBuiltinValueType.GraphAuxConverter() {
              @Override
              public Object desemiserialize(SemiserialSubvalue data) {
                return data.dispatch(
                    new SemiserialSubvalue.DefaultDispatcher<>() {
                      @Override
                      public Object handleRecord(SemiserialRecord s) {
                        final TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data =
                            s.data.mut();
                        final int pcount = constructor.getParameterCount();
                        Object args[] = new Object[pcount];
                        for (int i = 0; i < pcount; i++) {
                          final Parameter parameter = constructor.getParameters()[i];
                          final SemiserialSubvalue argData =
                              data.removeGet(new SemiserialString(parameter.getName()));
                          if (argData == null) {
                            throw new RuntimeException(
                                Format.format(
                                    "%s missing field %s", klass.getName(), parameter.getName()));
                          }
                          args[i] =
                              graphAuxConverters0.get(parameter.getType()).desemiserialize(argData);
                        }
                        return uncheck(() -> constructor.newInstance(args));
                      }
                    });
              }

              @Override
              public SemiserialSubvalue semiserialize(Object data) {
                final int pcount = constructor.getParameterCount();
                TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> out = new TSOrderedMap<>();
                for (int i = 0; i < pcount; i++) {
                  final Parameter parameter = constructor.getParameters()[i];
                  out.put(
                      new SemiserialString(parameter.getName()),
                      graphAuxConverters0
                          .get(parameter.getType())
                          .semiserialize(
                              uncheck(() -> klass.getField(parameter.getName()).get(data))));
                }
                return new SemiserialRecord(out);
              }
            });
      }
    }
    graphAuxConverters = graphAuxConverters0;
  }
}
