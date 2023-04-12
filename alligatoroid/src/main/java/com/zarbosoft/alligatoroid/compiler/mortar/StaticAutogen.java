package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.builtin.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.InlineType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.language.Access;
import com.zarbosoft.alligatoroid.compiler.model.language.Bind;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.model.language.Call;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.model.language.Local;
import com.zarbosoft.alligatoroid.compiler.model.language.Lower;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.model.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.model.language.Stage;
import com.zarbosoft.alligatoroid.compiler.model.language.Tuple;
import com.zarbosoft.alligatoroid.compiler.model.language.Wrap;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoDesemiAnyViaReflect;
import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoSemiAnyViaReflect;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class StaticAutogen {
  public static final ROMap<Class, InlineType> graphInlineTypeLookup;
  public static final ROMap<Class, ExportableType> autoExportableTypeLookup;
  public static final ROMap<Object, String> singletonExportableKeyLookup;
  public static final ROMap<String, Object> singletonExportableLookup;
  public static final ROMap<Class, ExportableType> detachedExportableTypeLookup;
  public static final ROMap<Class, MortarObjectImplType> autoMortarHalfObjectTypes;
  public static final ROMap<Class, MortarDataType> primitivePrototypeLookup;
  public static final LooseRecord builtin;
  public static final MortarDataType prototypeLanguageElement;
  public static final MortarObjectImplType prototypeValue;
  public static final MortarObjectImplType prototypeLocation;

  static {
    final WorkingMeta working = new WorkingMeta();

    InlineType tsListInlineType =
        new InlineType() {
          @Override
          public Object desemiserializeValue(
              ModuleCompileContext context,
              Desemiserializer typeDesemiserializer,
              TypeInfo type,
              SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleTuple(SemiserialTuple s) {
                    TSList out = new TSList();
                    for (SemiserialSubvalue subdata : s.values) {
                      out.add(
                          autoDesemiAnyViaReflect(
                              context, typeDesemiserializer, type.genericArgs[0], subdata));
                    }
                    return out;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserializeValue(
              long importCacheId,
              Semiserializer semiserializer,
              ROList<Object> path,
              ROList<String> accessPath,
              TypeInfo type,
              Object data) {
            TSList<SemiserialSubvalue> elementData = new TSList<>();
            for (int i = 0; i < ((ROList) data).size(); i++) {
              final Object el = ((ROList) data).get(i);
              SemiserialSubvalue subNonCollectionArg =
                  autoSemiAnyViaReflect(
                      semiserializer,
                      importCacheId,
                      type.genericArgs[0],
                      el,
                      path,
                      accessPath.mut().add(Integer.toString(i)));
              if (subNonCollectionArg != null) {
                elementData.add(subNonCollectionArg);
              } else {
                throw new Assertion();
              }
            }
            return SemiserialTuple.create(elementData);
          }
        };
    working.inlineTypeLookup.put(TSList.class, tsListInlineType);
    working.inlineTypeLookup.put(ROList.class, tsListInlineType);

    InlineType tsMapInlineType =
        new InlineType() {
          @Override
          public Object desemiserializeValue(
              ModuleCompileContext context,
              Desemiserializer typeDesemiserializer,
              TypeInfo type,
              SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleRecord(SemiserialRecord s) {
                    TSMap out = new TSMap();
                    for (ROPair<SemiserialSubvalue, SemiserialSubvalue> datum : s.data) {
                      out.put(
                          autoDesemiAnyViaReflect(
                              context, typeDesemiserializer, type.genericArgs[0], datum.first),
                          autoDesemiAnyViaReflect(
                              context, typeDesemiserializer, type.genericArgs[1], datum.second));
                    }
                    return out;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserializeValue(
              long importCacheId,
              Semiserializer semiserializer,
              ROList<Object> path,
              ROList<String> accessPath,
              TypeInfo type,
              Object data) {
            TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue> elementData = new TSOrderedMap<>();
            for (Object el0 : ((ROMap) data)) {
              Map.Entry el = (Map.Entry) el0;
              final SemiserialSubvalue k =
                  autoSemiAnyViaReflect(
                      semiserializer,
                      importCacheId,
                      type.genericArgs[0],
                      el.getKey(),
                      path,
                      accessPath);
              final SemiserialSubvalue v =
                  autoSemiAnyViaReflect(
                      semiserializer,
                      importCacheId,
                      type.genericArgs[1],
                      el.getValue(),
                      path,
                      accessPath);
              if (k == null || v == null) {
                throw new Assertion();
              }
              elementData.putNew(k, v);
            }
            return SemiserialRecord.create(elementData);
          }
        };
    working.inlineTypeLookup.put(TSMap.class, tsMapInlineType);
    working.inlineTypeLookup.put(ROMap.class, tsMapInlineType);

    InlineType intConverter =
        new InlineType() {
          @Override
          public Object desemiserializeValue(
              ModuleCompileContext context,
              Desemiserializer typeDesemiserializer,
              TypeInfo type,
              SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<Object>() {
                  @Override
                  public Object handleInt(SemiserialInt s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserializeValue(
              long importCacheId,
              Semiserializer semiserializer,
              ROList<Object> path,
              ROList<String> accessPath,
              TypeInfo type,
              Object value) {
            return SemiserialInt.create((Integer) value);
          }
        };
    working.inlineTypeLookup.put(int.class, intConverter);
    working.inlineTypeLookup.put(Integer.class, intConverter);
    working.detachedExportType(int.class, intConverter);
    working.detachedExportType(Integer.class, intConverter);
    working.primitivePrototypeLookup.put(int.class, MortarPrimitiveAll.typeInt);

    InlineType boolConverter =
        new InlineType() {
          @Override
          public Object desemiserializeValue(
              ModuleCompileContext context,
              Desemiserializer typeDesemiserializer,
              TypeInfo type,
              SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleBool(SemiserialBool s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserializeValue(
              long importCacheId,
              Semiserializer semiserializer,
              ROList<Object> path,
              ROList<String> accessPath,
              TypeInfo type,
              Object data) {
            return SemiserialBool.create((Boolean) data);
          }
        };
    working.inlineTypeLookup.put(boolean.class, boolConverter);
    working.inlineTypeLookup.put(Boolean.class, boolConverter);
    working.detachedExportType(boolean.class, boolConverter);
    working.detachedExportType(Boolean.class, boolConverter);
    working.primitivePrototypeLookup.put(boolean.class, MortarPrimitiveAll.typeBool);

    InlineType stringConverter =
        new InlineType() {
          @Override
          public Object desemiserializeValue(
              ModuleCompileContext context,
              Desemiserializer typeDesemiserializer,
              TypeInfo type,
              SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleString(SemiserialString s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserializeValue(
              long importCacheId,
              Semiserializer semiserializer,
              ROList<Object> path,
              ROList<String> accessPath,
              TypeInfo type,
              Object data) {
            return SemiserialString.create((String) data);
          }
        };
    working.inlineTypeLookup.put(String.class, stringConverter);
    working.detachedExportType(String.class, stringConverter);
    working.primitivePrototypeLookup.put(String.class, MortarPrimitiveAll.typeString);

    working.primitivePrototypeLookup.put(byte.class, MortarPrimitiveAll.typeByte);
    working.primitivePrototypeLookup.put(byte[].class, MortarPrimitiveAll.typeBytes);

    working.generateMortarType(ModuleId.class);
    {
      // working.singletonExportable(nullValue);
      // working.mortarType(nullValue.getClass(), nullValue.type);
      // working.singletonExportable(nullValue.type);
    }
    for (Class<BuiltinAutoExportable> languageElement :
        new Class[] {
          Access.class,
          Bind.class,
          Block.class,
          com.zarbosoft.alligatoroid.compiler.model.language.Builtin.class,
          Call.class,
          LiteralString.class,
          LiteralBool.class,
          Local.class,
          Record.class,
          RecordElement.class,
          Tuple.class,
          Stage.class,
          Lower.class,
          Wrap.class,
        }) {
      working.generateBuiltinExportableType(languageElement);
      working.generateMortarType(languageElement);
    }
    for (Class klass :
        new Class[] {
          BundleValue.class,
          Location.class,
          LocalModuleId.class,
          RemoteModuleId.class,
          BundleModuleSubId.class,
          ImportId.class,
          JavaQualifiedName.class,
          JavaInternalName.class,
        }) {
      if (NoExportValue.class.isAssignableFrom(klass)) {
        throw new Assertion();
      }
      if (!BuiltinAutoExportable.class.isAssignableFrom(klass)) {
        throw new Assertion();
      }
      working.generateBuiltinExportableType(klass);
      working.generateMortarType(klass);
    }

    builtin = StaticAutogen.aggregateBuiltinForGraph(working, Builtin.class, "");

    // Done
    graphInlineTypeLookup = working.inlineTypeLookup;
    autoExportableTypeLookup = working.autoExportableTypeLookup;
    detachedExportableTypeLookup = working.detachedExportableTypeLookup;
    singletonExportableKeyLookup = working.singletonBuiltinKeyLookup;
    singletonExportableLookup = working.singletonBuiltinLookup;
    autoMortarHalfObjectTypes = working.autoMortarHalfDataTypes;
    primitivePrototypeLookup = working.primitivePrototypeLookup;
    prototypeLanguageElement = autoMortarHalfObjectTypes.get(LanguageElement.class);
    prototypeValue = autoMortarHalfObjectTypes.get(Value.class);
    prototypeLocation = autoMortarHalfObjectTypes.get(Location.class);
  }

  /**
   * Must be called during initialization (single thread)!
   *
   * @param method
   * @param working.autoMortarHalfDataTypes
   * @param working.builtinSingletonIndexes
   * @param builtinSingletons0
   * @return
   */
  public static FuncInfo funcDescriptor(WorkingMeta working, Method method) {
    boolean needsLocation = false;
    TSList<ROPair<Object, MortarDataType>> argTypes = new TSList<>();
    for (int i = 0; i < method.getParameters().length; ++i) {
      Parameter parameter = method.getParameters()[i];
      if (i == 0 && parameter.getType() == Location.class) {
        needsLocation = true;
      }
      MortarDataType paramType = dataDescriptor(working, parameter.getType());
      argTypes.add(new ROPair<>(parameter.getName(), paramType));
    }

    MortarDataType retType = dataDescriptor(working, method.getReturnType());

    return new FuncInfo(
        method.getName(),
        JavaBytecodeUtils.qualifiedNameFromClass(method.getDeclaringClass()),
        argTypes,
        retType,
        needsLocation);
  }

  /** Must be called during initialization (single thread)! */
  public static MortarDataType dataDescriptor(WorkingMeta working, Class klass) {
    if (klass == void.class) {
      return NullType.type;
    } else if (klass.isPrimitive()) {
      if (klass == byte.class) {
        return MortarPrimitiveAll.typeByte;
      } else if (klass == boolean.class) {
        return MortarPrimitiveAll.typeBool;
      } else if (klass == int.class) {
        return MortarPrimitiveAll.typeInt;
      } else {
        throw new Assertion();
      }
    } else if (klass == String.class) {
      return MortarPrimitiveAll.typeString;
    } else if (klass == byte[].class) {
      return MortarPrimitiveAll.typeBytes;
    } else {
      return working.generateMortarType(klass);
    }
  }

  /** Must be called during initialization (single thread)! */
  public static MortarObjectField fieldDescriptor(
      MortarObjectInnerType parent, String name, WorkingMeta working, Class klass) {
    if (klass == void.class) {
      return NullMortarField.field;
    } else if (klass.isPrimitive()) {
      final MortarPrimitiveAll t;
      if (klass == byte.class) {
        t = MortarPrimitiveAll.typeByte;
      } else if (klass == boolean.class) {
        t = MortarPrimitiveAll.typeBool;
      } else if (klass == int.class) {
        t = MortarPrimitiveAll.typeInt;
      } else {
        throw new Assertion();
      }
      return new MortarPrimitiveFieldAll(parent, name, t);
    } else if (klass == String.class) {
      return new MortarPrimitiveFieldAll(parent, name, MortarPrimitiveAll.typeString);
    } else if (klass == byte[].class) {
      return new MortarPrimitiveFieldAll(parent, name, MortarPrimitiveAll.typeBytes);
    } else {
      final MortarObjectImplType t = working.generateMortarType(klass);
      return new MortarObjectImplField(parent, name, t.meta, t.fields);
    }
  }

  private static Value autoMortarHalfStaticMethodType(
      WorkingMeta working, Class klass, String name) {
    Method method = null;
    for (Method checkMethod : klass.getMethods()) {
      if (!checkMethod.getName().equals(name)) {
        continue;
      }
      method = checkMethod;
      break;
    }
    if (method == null) {
      throw Assertion.format("builtin wrap [%s] function [%s] missing", klass.getName(), name);
    }
    return MortarStaticMethodTypestate.typestate.typestate_constAsValue(funcDescriptor(working, method));
  }

  private static LooseRecord aggregateBuiltinForGraph(
      WorkingMeta working, Class klass, String path) {
    TSOrderedMap<Object, EvaluateResult> values = new TSOrderedMap<>();
    for (Field f : klass.getDeclaredFields()) {
      if (!Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      String name = f.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      Object data = uncheck(() -> f.get(null));
      if (data.getClass().isAnnotationPresent(BuiltinAggregate.class)) {
        final String key = path + "/" + name;
        final LooseRecord value = aggregateBuiltinForGraph(working, data.getClass(), key);
        values.put(name, EvaluateResult.pure(value));
        working.singletonBuiltinKeyLookup.put(value, key);
        working.singletonBuiltinLookup.put(key, value);
      } else {
        working.registerSingletonBuiltinExportable((BuiltinSingletonExportable) data);
        if (data instanceof Value) {
          values.put(name, EvaluateResult.pure((MortarDataValueConst) data));
        } else {
          final MortarObjectImplType type = working.generateMortarType(data.getClass());
          values.put(name, EvaluateResult.pure(type.type_constAsValue(data)));
        }
      }
    }
    for (Method m : klass.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) {
        continue;
      }
      String name = m.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      values.put(
          name, EvaluateResult.pure(autoMortarHalfStaticMethodType(working, klass, m.getName())));
    }
    return new LooseRecord(values);
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface WrapExpose {}

  @Retention(RetentionPolicy.RUNTIME)
  public @interface BuiltinAggregate {}

  public static class BuiltinContext {
    public final ModuleCompileContext context;
    public final Location location;

    public BuiltinContext(ModuleCompileContext context, Location location) {
      this.context = context;
      this.location = location;
    }
  }

  private static class WorkingMeta {
    public final TSMap<Class, ExportableType> detachedExportableTypeLookup = new TSMap<>();
    public final TSMap<Class, MortarObjectImplType> autoMortarHalfDataTypes = new TSMap<>();
    public final TSMap<Class, ExportableType> autoExportableTypeLookup = new TSMap<>();
    public final TSMap<Class, InlineType> inlineTypeLookup = new TSMap<>();
    public final TSMap<Object, String> singletonBuiltinKeyLookup = new TSMap<>();
    public final TSMap<String, Object> singletonBuiltinLookup = new TSMap<>();
    public final TSMap<Class, MortarDataType> primitivePrototypeLookup = new TSMap<>();
    public int singletonCount;

    private void registerSingletonBuiltinExportable(BuiltinSingletonExportable e) {
      final String key = "_singleton_" + singletonCount++;
      registerSingletonBuiltinExportable(key, e);
    }

    private void registerSingletonBuiltinExportable(String key, BuiltinSingletonExportable e) {
      singletonBuiltinKeyLookup.put(e, key);
      singletonBuiltinLookup.put(key, e);
    }

    private void generateBuiltinExportableType(Class klass) {
      final BuiltinAutoExportableType type = new BuiltinAutoExportableType(klass);
      autoExportableTypeLookup.put(klass, type);
      {
        final Constructor[] constructors = klass.getConstructors();
        if (constructors.length != 1) {
          throw new Assertion();
        }
        if (constructors[0].getParameterCount() != 0) {
          throw new Assertion();
        }
        for (Field field : klass.getFields()) {
          if (Modifier.isStatic(field.getModifiers())) {
            continue;
          }
          if (Modifier.isFinal(field.getModifiers())) {
            throw new Assertion();
          }
          if (!Modifier.isPublic(field.getModifiers())) {
            throw new Assertion();
          }
        }
      }
      registerSingletonBuiltinExportable(klass.getCanonicalName(), type);
    }

    public MortarObjectImplType generateMortarType(Class klass) {
      MortarObjectImplType out = autoMortarHalfDataTypes.getOpt(klass);
      if (out == null) {
        TSMap<Object, MortarObjectField> fields = new TSMap<>();
        TSList<MortarObjectInnerType> inherits = new TSList<>();
        final MortarObjectInnerType innerType =
            new MortarObjectInnerType(JavaBytecodeUtils.qualifiedNameFromClass(klass), inherits);
        if (klass != MortarDataValueVariableStack.class) {
          for (Method method : klass.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
              continue;
            }
            if (!method.isAnnotationPresent(WrapExpose.class)) {
              continue;
            }
            fields.putNew(
                method.getName(),
                new MortarObjectMethodAll(innerType, funcDescriptor(this, method)));
          }
        }
        for (Field field : klass.getDeclaredFields()) {
          MortarDataType dataType = dataDescriptor(this, field.getType());
          String fieldName = field.getName();
          fields.putNew(fieldName, fieldDescriptor(innerType, fieldName, this, field.getType()));
        }
        if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
          inherits.add(generateMortarType(klass.getSuperclass()).meta);
        }
        for (Class iface : klass.getInterfaces()) {
          inherits.add(generateMortarType(iface).meta);
        }
        MortarObjectImplType out1 = new MortarObjectImplType(innerType, fields);
        autoMortarHalfDataTypes.put(klass, out1);
        out = out1;
      }
      return out;
    }

    public void detachedExportType(Class klass, InlineType inlineType) {
      final DetachedExportableType t =
          new DetachedExportableType(TypeInfo.fromClass(klass), inlineType);
      detachedExportableTypeLookup.put(klass, t);
      registerSingletonBuiltinExportable(t);
    }
  }

  public static class DetachedExportableType implements ExportableType, BuiltinSingletonExportable {
    private final InlineType inlineType;
    private final TypeInfo type;

    public DetachedExportableType(TypeInfo type, InlineType inlineType) {
      this.inlineType = inlineType;
      this.type = type;
    }

    @Override
    public SemiserialSubvalue graphSemiserializeBody(
        long importCacheId,
        Semiserializer semiserializer,
        ROList<Object> path,
        ROList<String> accessPath,
        Object value) {
      return inlineType.semiserializeValue(
          importCacheId, semiserializer, path, accessPath, type, value);
    }

    @Override
    public Object graphDesemiserializeBody(
        ModuleCompileContext context,
        Desemiserializer typeDesemiserializer,
        SemiserialSubvalue data) {
      return inlineType.desemiserializeValue(context, typeDesemiserializer, type, data);
    }
  }

  public static class FuncInfo {
    public final String name;
    public final JavaQualifiedName base;
    public final ROList<ROPair<Object, MortarDataType>> arguments;
    public final MortarDataType returnType;
    public final boolean needsLocation;

    public FuncInfo(
        String name,
        JavaQualifiedName base,
        TSList<ROPair<Object, MortarDataType>> arguments,
        MortarDataType returnType,
        boolean needsLocation) {
      this.name = name;
      this.base = base;
      this.arguments = arguments;
      this.returnType = returnType;
      this.needsLocation = needsLocation;
    }

    public ROList<JavaDataDescriptor> argDescriptor() {
      TSList<JavaDataDescriptor> out = new TSList<>();
      for (ROPair<Object, MortarDataType> argumentType : arguments) {
        out.add(argumentType.second.type_jvmDesc());
      }
      return out;
    }
  }
}
