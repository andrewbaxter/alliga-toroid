package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.MortarBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.Meta.LANGUAGE;
import static com.zarbosoft.alligatoroid.compiler.Meta.OTHER_AUTO_GRAPH;
import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfDataType;
import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfStaticMethodType;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Builtin {
  public static final ROMap<Exportable, String> builtinToSemiKey;
  public static final ROMap<String, Exportable> semiKeyToBuiltin;
  public static final ROMap<Class, Exportable> autoBuiltinExportTypes;
  public static final TSMap<ObjId, Integer> builtinSingletonIndexes;
  public static final ROList<Object> builtinSingletons;
  public static LooseRecord builtin;

  static {
    //// Builtin value lookups for graph work
    // =============================
    TSMap<Exportable, String> builtinToSemiKey0 = new TSMap<>();
    TSMap<String, Exportable> semiKeyToBuiltin0 = new TSMap<>();
    TSMap<Class, Exportable> autoBuiltinExportTypes0 = new TSMap<>();
    TSMap<ObjId, Integer> builtinSingletonIndexes0 = new TSMap<>();
    TSList<Object> builtinSingletons0 = new TSList<>();
    builtin =
        aggregateBuiltinForGraph(
            builtinToSemiKey0,
            semiKeyToBuiltin0,
            builtinSingletonIndexes0,
            builtinSingletons0,
            MortarBuiltin.class,
            "");
    for (ROPair<String, Exportable> o : new ROPair[] {new ROPair("null", ConstNull.value)}) {
      processSingleton(builtinSingletonIndexes0, builtinSingletons0, o);
      String key = "_singleton_" + o.first;
      builtinToSemiKey0.put(o.second, key);
      semiKeyToBuiltin0.put(key, o.second);
    }
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
        autoBuiltinExportTypes0.put(klass, type);
        String builtinKey = klass.getCanonicalName();
        builtinToSemiKey0.put(type, builtinKey);
        semiKeyToBuiltin0.put(builtinKey, type);
        Meta.autoMortarHalfDataType(klass);
      }
    };
    for (ROPair<Class, Exportable> builtinExportable :
        new ROPair[] {
          new ROPair<>(JVMSharedJVMName.class, JVMSharedJVMName.exportableType),
          new ROPair<>(JVMSharedNormalName.class, JVMSharedNormalName.exportableType),
        }) {
      final String key = builtinExportable.first.getCanonicalName();
      semiKeyToBuiltin0.put(key, builtinExportable.second);
      builtinToSemiKey0.put(builtinExportable.second, key);
    }
    builtinToSemiKey = builtinToSemiKey0;
    semiKeyToBuiltin = semiKeyToBuiltin0;
    autoBuiltinExportTypes = autoBuiltinExportTypes0;
    builtinSingletonIndexes = builtinSingletonIndexes0;
    builtinSingletons = builtinSingletons0;
  }

  public static Object getBuiltinSingleton(int index) {
    return builtinSingletons.get(index);
  }

  public static MortarTargetModuleContext.HalfLowerResult halfLowerSingleton(Object data) {
    Integer index = builtinSingletonIndexes.getOpt(new ObjId(data));
    if (index == null) return null;
    return new MortarTargetModuleContext.HalfLowerResult(
        Meta.autoMortarHalfDataTypes.get(data.getClass()),
        new JVMSharedCode()
            .add(
                JVMSharedCode.callStaticMethod(
                    -1,
                    JVMSharedJVMName.fromClass(Builtin.class),
                    "getBuiltinSingleton",
                    JVMSharedFuncDescriptor.fromParts(
                        JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.INT))));
  }

  private static void processSingleton(
      TSMap<ObjId, Integer> builtinSingletonIndexes,
      TSList<Object> builtinSingletons,
      Object data) {
    int singletonIndex = builtinSingletons.size();
    builtinSingletonIndexes.put(new ObjId(data), singletonIndex);
    builtinSingletons.add(data);
    Meta.autoMortarHalfDataType(data.getClass());
  }

  private static LooseRecord aggregateBuiltinForGraph(
      TSMap<Exportable, String> builtinToSemikey,
      TSMap<String, Exportable> semikeyToBuiltin,
      TSMap<ObjId, Integer> builtinSingletonIndexes,
      TSList<Object> builtinSingletons,
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
      if (data.getClass().isAnnotationPresent(Aggregate.class)) {
        final String key = path + "/" + name;
        final LooseRecord value =
            aggregateBuiltinForGraph(
                builtinToSemikey,
                semikeyToBuiltin,
                builtinSingletonIndexes,
                builtinSingletons,
                data.getClass(),
                key);
        values.put(name, EvaluateResult.pure(value));
        builtinToSemikey.put(value, key);
        semikeyToBuiltin.put(key, value);
      } else {
        processSingleton(builtinSingletonIndexes, builtinSingletons, data);
        if (data instanceof VariableDataStackValue) {
          values.put(name, EvaluateResult.pure((VariableDataStackValue) data));
        } else {
          values.put(name, EvaluateResult.pure(new MortarObject(autoMortarHalfDataType(data.getClass()), carry(data))));
        }
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

  public static MortarCarry carry(Object data) {
  return new MortarCarry() {
    @Override
    public boolean isWhole() {
    return true;
    }

    @Override
    public Object whole() {
    return data;
    }

    @Override
    public JVMSharedCodeElement half(EvaluationContext context) {
      throw new Assertion();
    }

    @Override
    public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
      throw new Assertion();
    }
  };
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface Aggregate {}
}
