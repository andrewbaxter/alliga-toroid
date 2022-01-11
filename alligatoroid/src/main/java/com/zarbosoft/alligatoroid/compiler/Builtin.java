package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.value.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.AutoGraphValueType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarBuiltin;
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
  public static ROMap<Value, String> builtinToSemiKey;
  public static ROMap<String, Value> semiKeyToBuiltin;
  public static ROMap<Class, Value> builtinToBuiltinType;

  static {
    //// Builtin value lookups for graph work
    // =============================
    TSMap<Value, String> builtinToSemiKey = new TSMap<>();
    TSMap<String, Value> semiKeyToBuiltin = new TSMap<>();
    TSMap<Class, Value> builtinToBuiltinType = new TSMap<>();
    builtin = aggregateBuiltinForGraph(builtinToSemiKey, semiKeyToBuiltin, MortarBuiltin.class, "");
    for (Class<AutoGraphMixin> languageElement : LANGUAGE) {
      AutoGraphMixin.assertFieldsOk(languageElement);
      builtinToBuiltinType.put(languageElement, new AutoGraphValueType(languageElement));
    }
    for (Class<AutoGraphMixin> klass : OTHER_AUTO_GRAPH) {
      AutoGraphMixin.assertFieldsOk(klass);
      builtinToBuiltinType.put(klass, new AutoGraphValueType(klass));
    }
    Builtin.builtinToSemiKey = builtinToSemiKey;
    Builtin.semiKeyToBuiltin = semiKeyToBuiltin;
    Builtin.builtinToBuiltinType = builtinToBuiltinType;
  }

  private static LooseRecord aggregateBuiltinForGraph(
      TSMap<Value, String> semiBuiltinLookup,
      TSMap<String, Value> desemiBuiltinLookup,
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
