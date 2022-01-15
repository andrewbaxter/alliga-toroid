package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.BuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeOther;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.Meta.LANGUAGE;
import static com.zarbosoft.alligatoroid.compiler.Meta.OTHER_AUTO_GRAPH;
import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfStaticMethodType;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Builtin {
  public static LooseRecord builtin;
  public static ROMap<Exportable, String> builtinToSemiKey;
  public static ROMap<String, Exportable> semiKeyToBuiltin;
  public static ROMap<Class, Exportable> autoBuiltinTypes;

  static {
    //// Builtin value lookups for graph work
    // =============================
    TSMap<Exportable, String> builtinToSemiKey = new TSMap<>();
    TSMap<String, Exportable> semiKeyToBuiltin = new TSMap<>();
    TSMap<Class, Exportable> autoBuiltinType = new TSMap<>();
    builtin = aggregateBuiltinForGraph(builtinToSemiKey, semiKeyToBuiltin, MortarBuiltin.class, "");
    new Object() {
      {
        for (Class<AutoBuiltinExportable> languageElement : LANGUAGE) {
          process(languageElement);
        }
        for (Class<AutoBuiltinExportable> klass : OTHER_AUTO_GRAPH) {
          process(klass);
        }
      }

      private void process(Class klass) {
        final AutoBuiltinExportableType type = new AutoBuiltinExportableType(klass);
        autoBuiltinType.put(klass, type);
        String builtinKey = klass.getCanonicalName();
        builtinToSemiKey.put(type, builtinKey);
        semiKeyToBuiltin.put(builtinKey, type);
      }
    };
    for (ROPair<Class, BuiltinExportableType> builtinExportable :
        new ROPair[] {
          new ROPair<>(JVMSharedJVMName.class, JVMSharedJVMName.exportableType),
          new ROPair<>(JVMSharedNormalName.class, JVMSharedNormalName.exportableType),
        }) {
      final String key = builtinExportable.first.getCanonicalName();
      semiKeyToBuiltin.put(key, builtinExportable.second);
      builtinToSemiKey.put(builtinExportable.second, key);
    }
    Builtin.builtinToSemiKey = builtinToSemiKey;
    Builtin.semiKeyToBuiltin = semiKeyToBuiltin;
    Builtin.autoBuiltinTypes = autoBuiltinType;
  }

  private static LooseRecord aggregateBuiltinForGraph(
      TSMap<Exportable, String> builtinToSemikey,
      TSMap<String, Exportable> semikeyToBuiltin,
      Class klass,
      String path) {
    TSOrderedMap<Object, EvaluateResult> values = new TSOrderedMap<>();
    for (Field f : klass.getDeclaredFields()) {
      if (!Modifier.isStatic(f.getModifiers())) continue;
      String name = f.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      Object data = uncheck(() -> f.get(null));
      if (data instanceof Value) {
        values.put(name, EvaluateResult.pure((Value) data));
      } else if (data.getClass().isAnnotationPresent(Aggregate.class)) {
        final String key = path + "/" + name;
        final LooseRecord value =
            aggregateBuiltinForGraph(builtinToSemikey, semikeyToBuiltin, data.getClass(), key);
        values.put(name, EvaluateResult.pure(value));
        builtinToSemikey.put(value, key);
        semikeyToBuiltin.put(key, value);
      } else {
        values.put(name, EvaluateResult.pure(new WholeOther(data)));
      }
    }
    for (Method m : klass.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) continue;
      String name = m.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      values.put(name, EvaluateResult.pure(autoMortarHalfStaticMethodType(klass, m.getName())));
    }
    return new LooseRecord(values);
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface Aggregate {}
}
