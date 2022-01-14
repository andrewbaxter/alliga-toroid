package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoExportableType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

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
  public static ROMap<Class, Exportable> builtinToBuiltinType;

  static {
    //// Builtin value lookups for graph work
    // =============================
    TSMap<Exportable, String> builtinToSemiKey = new TSMap<>();
    TSMap<String, Exportable> semiKeyToBuiltin = new TSMap<>();
    TSMap<Class, Exportable> builtinToBuiltinType = new TSMap<>();
    builtin = aggregateBuiltinForGraph(builtinToSemiKey, semiKeyToBuiltin, MortarBuiltin.class, "");
    for (Class<AutoExportable> languageElement : LANGUAGE) {
      AutoExportable.assertFieldsOk(languageElement);
      builtinToBuiltinType.put(languageElement, new AutoExportableType(languageElement));
    }
    for (Class<AutoExportable> klass : OTHER_AUTO_GRAPH) {
      AutoExportable.assertFieldsOk(klass);
      builtinToBuiltinType.put(klass, new AutoExportableType(klass));
    }
    Builtin.builtinToSemiKey = builtinToSemiKey;
    Builtin.semiKeyToBuiltin = semiKeyToBuiltin;
    Builtin.builtinToBuiltinType = builtinToBuiltinType;
  }

  private static LooseRecord aggregateBuiltinForGraph(
      TSMap<Exportable, String> semiBuiltinLookup,
      TSMap<String, Exportable> desemiBuiltinLookup,
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
      } else {
        final String key = path + "/" + name;
        final LooseRecord value =
            aggregateBuiltinForGraph(semiBuiltinLookup, desemiBuiltinLookup, data.getClass(), key);
        values.put(name, EvaluateResult.pure(value));
        semiBuiltinLookup.put(value, key);
        desemiBuiltinLookup.put(key, value);
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
}
