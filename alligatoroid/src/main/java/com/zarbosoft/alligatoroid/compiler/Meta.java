package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.builtin.Builtin;
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
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
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
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarBytesType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStaticMethodType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
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

import static com.zarbosoft.alligatoroid.compiler.builtin.Builtin.nullType;
import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoDesemiAnyViaReflect;
import static com.zarbosoft.alligatoroid.compiler.inout.graph.AutoSemiUtils.autoSemiAnyViaReflect;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Meta {
  public static final ROMap<Class, InlineType> inlineTypeLookup;
  public static final ROMap<Class, ExportableType> autoExportableTypeLookup;
  public static final ROMap<Object, String> singletonExportableKeyLookup;
  public static final ROMap<String, Object> singletonExportableLookup;
  public static final ROMap<Class, ExportableType> detachedExportableTypeLookup;
  public static ROMap<Class, MortarSimpleDataType> autoMortarHalfDataTypes;
  public static LooseRecord builtin;

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
              } else throw new Assertion();
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
              if (k == null || v == null) throw new Assertion();
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
      if (NoExportValue.class.isAssignableFrom(klass)) throw new Assertion();
      if (!BuiltinAutoExportable.class.isAssignableFrom(klass)) throw new Assertion();
      working.generateBuiltinExportableType(klass);
      working.generateMortarType(klass);
    }

    Meta.builtin = Meta.aggregateBuiltinForGraph(working, Builtin.class, "");

    // Done
    inlineTypeLookup = working.inlineTypeLookup;
    autoExportableTypeLookup = working.autoExportableTypeLookup;
    detachedExportableTypeLookup = working.detachedExportableTypeLookup;
    singletonExportableKeyLookup = working.singletonBuiltinKeyLookup;
    singletonExportableLookup = working.singletonBuiltinLookup;
    autoMortarHalfDataTypes = working.autoMortarHalfDataTypes;
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

  /*
  public static <T> T parseCheckArgument(TSList<Error> errors, TSList<String> path, Class<T> type, Object object) {
    if (type.isAssignableFrom(object.getClass())) {
      return (T)object;
    } else {
      Record record = (Record)object;
      TSSet<Object> consumed = new TSSet<>();
      Constructor<?> constructor = type.getConstructors()[0];
      Object[] args = new Object[constructor.getParameters().length];
      for (int i = 0; i < constructor.getParameters().length; i++) {
        Parameter parameter = constructor.getParameters()[i];
        Object value = record.data.getOpt(parameter.getName());
        if (value == null) {
          errors.add(Error.recordMissingField(path, parameter.getName()));
          continue;
        }
        consumed.add(parameter.getName());
        args[i] = parseCheckArgument(errors,path.mut().add(parameter.getName()), parameter.getType(), value);
      }
      for (Object key : record.data.keys()) {
        if (consumed.contains(key)) continue;
        errors.add(Error.recordExtraField( path, key));
      }
      if (errors.none()) {
        return (T) uncheck(() -> constructor.newInstance(args));
      } else {
        return null;
      }
    }
  }
  public static <T> T parseCheckArgument(Class<T> type, Object object) {
    TSList<Error> errors = new TSList<>();
    T out = parseCheckArgument(errors,new TSList<>(),type,object);
    if (errors.some()) throw new MultiError(errors);
    return out;
  }
   */

  /**
   * Must be called during initialization (single thread)!
   *
   * @param autoMortarHalfDataTypes
   * @param klass
   * @param working.builtinSingletonIndexes
   * @param builtinSingletons0
   * @return
   */
  public static MortarSimpleDataType dataDescriptor(WorkingMeta working, Class klass) {
    if (klass == void.class) {
      return nullType;
    } else if (klass == String.class) {
      return MortarStringType.type;
    } else if (klass == byte[].class) {
      return MortarBytesType.type;
    } else {
      return working.generateMortarType(klass);
    }
  }

  private static Value autoMortarHalfStaticMethodType(
      WorkingMeta working, Class klass, String name) {
    Method method = null;
    for (Method checkMethod : klass.getMethods()) {
      if (!checkMethod.getName().equals(name)) continue;
      method = checkMethod;
      break;
    }
    if (method == null)
      throw Assertion.format("builtin wrap [%s] function [%s] missing", klass.getName(), name);
    return MortarStaticMethodType.type.constAsValue(funcDescriptor(working, method));
  }

  private static LooseRecord aggregateBuiltinForGraph(
      WorkingMeta working, Class klass, String path) {
    TSOrderedMap<Object, EvaluateResult> values = new TSOrderedMap<>();
    for (Field f : klass.getDeclaredFields()) {
      if (!Modifier.isStatic(f.getModifiers())) continue;
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
          values.put(name, EvaluateResult.pure((ConstDataValue) data));
        } else {
          final MortarDataType type = working.generateMortarType(data.getClass());
          values.put(name, EvaluateResult.pure(type.constAsValue(data)));
        }
      }
    }
    for (Method m : klass.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) continue;
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
    public final TSMap<Class, MortarSimpleDataType> autoMortarHalfDataTypes = new TSMap<>();
    public final TSMap<Class, ExportableType> autoExportableTypeLookup = new TSMap<>();
    public final TSMap<Class, InlineType> inlineTypeLookup = new TSMap<>();
    public final TSMap<Object, String> singletonBuiltinKeyLookup = new TSMap<>();
    public final TSMap<String, Object> singletonBuiltinLookup = new TSMap<>();
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
        if (constructors.length != 1) throw new Assertion();
        if (constructors[0].getParameterCount() != 0) throw new Assertion();
        for (Field field : klass.getFields()) {
          if (Modifier.isStatic(field.getModifiers())) continue;
          if (Modifier.isFinal(field.getModifiers())) throw new Assertion();
          if (!Modifier.isPublic(field.getModifiers())) throw new Assertion();
        }
      }
      registerSingletonBuiltinExportable(klass.getCanonicalName(), type);
    }

    public MortarSimpleDataType generateMortarType(Class klass) {
      MortarSimpleDataType out = autoMortarHalfDataTypes.getOpt(klass);
      if (out == null) {
        TSMap<Object, MortarObjectFieldType> fields = new TSMap<>();
        TSList<MortarDataType> inherits = new TSList<>();
        MortarObjectType out1 =
            MortarObjectType.create(JavaBytecodeUtils.qualifiedNameFromClass(klass), fields, inherits);
        autoMortarHalfDataTypes.put(klass, out1);
        if (klass != VariableDataValue.class)
          for (Method method : klass.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (!method.isAnnotationPresent(WrapExpose.class)) continue;
            fields.putNew(
                method.getName(), new MortarMethodFieldType(funcDescriptor(this, method)));
          }
        for (Field field : klass.getDeclaredFields()) {
          MortarSimpleDataType dataType = dataDescriptor(this, field.getType());
          String fieldName = field.getName();
          fields.putNew(fieldName, dataType);
        }
        if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
          inherits.add(generateMortarType(klass.getSuperclass()));
        }
        for (Class iface : klass.getInterfaces()) {
          inherits.add(generateMortarType(iface));
        }
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
        ROList<ROPair<Object, MortarDataType>> arguments,
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
        out.add(argumentType.second.jvmDesc());
      }
      return out;
    }
  }
}
