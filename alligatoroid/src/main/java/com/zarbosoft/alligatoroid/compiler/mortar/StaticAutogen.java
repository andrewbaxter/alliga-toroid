package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.builtin.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exporter;
import com.zarbosoft.alligatoroid.compiler.inout.graph.InlineType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
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
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Evaluation2Context;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
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
  public static final ROMap<Class, Exporter> autoExportableTypeLookup;
  public static final ROMap<ObjId<Object>, Integer> singletonExportableKeyLookup;
  public static final ROList<Object> singletonExportableLookup;
  public static final ROMap<Class, Exporter> detachedExportableTypeLookup;
  public static final ROMap<Class, MortarObjectImplType> autoMortarObjectTypes;
  public static final ROMap<Class, MortarDataType> mortarPrimitiveTypes;
  public static final Value builtin;
  public static final MortarDataType typeLanguageElement;
  public static final MortarObjectImplType typeValue;
  public static final MortarObjectImplType typeLocation;

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

    {
      // working.singletonExportable(nullValue);
      // working.mortarType(nullValue.getClass(), nullValue.type);
      // working.singletonExportable(nullValue.type);
    }

    // Gen type only
    working.autogenMortarObjectType(ModuleId.class);
    working.autogenMortarObjectType(JavaBytecode.class);
    working.autogenMortarObjectType(Evaluation2Context.class);

    // Gen type + exporter
    for (Class klass :
        new Class[] {
          // Language elements
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

          // Others
          BundleValue.class,
          Location.class,
          LocalModuleId.class,
          RemoteModuleId.class,
          BundleModuleSubId.class,
          ImportId.class,
          JavaQualifiedName.class,
          JavaInternalName.class,
          JavaDataDescriptor.class,
        }) {
      if (NoExportValue.class.isAssignableFrom(klass)) {
        throw new Assertion();
      }
      if (!BuiltinAutoExportable.class.isAssignableFrom(klass)) {
        throw new Assertion();
      }
      working.autogenExporter(klass);
      working.autogenMortarObjectType(klass);
    }

    // Done
    graphInlineTypeLookup = working.inlineTypeLookup;
    autoExportableTypeLookup = working.autoExportableTypeLookup;
    detachedExportableTypeLookup = working.detachedExportableTypeLookup;
    singletonExportableKeyLookup = working.singletonBuiltinKeyLookup;
    singletonExportableLookup = working.singletonBuiltinLookup;
    autoMortarObjectTypes = working.autoMortarHalfDataTypes;
    mortarPrimitiveTypes = working.primitivePrototypeLookup;
    typeLanguageElement = autoMortarObjectTypes.get(LanguageElement.class);
    typeValue = autoMortarObjectTypes.get(Value.class);
    typeLocation = autoMortarObjectTypes.get(Location.class);

    final ROPair<MortarDataType, Object> builtin0 =
        StaticAutogen.autogenBuiltinAggregate(working, new Builtin(), "");
    builtin = builtin0.first.type_constAsValue(builtin0.second);
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
      return working.autogenMortarObjectType(klass);
    }
  }

  private static ROPair<MortarRecordFieldable, FuncInfo> autoMortarHalfStaticMethodType(
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
    return new ROPair<>(MortarStaticMethodTypestate.typestate, funcDescriptor(working, method));
  }

  private static ROPair<MortarDataType, Object> autogenBuiltinAggregate(
      WorkingMeta working, Object parentData, String path) {
    TSList<ROPair<Object, MortarRecordField>> fields = new TSList<>();
    TSList<Object> values = new TSList<>();
    for (Field f : parentData.getClass().getDeclaredFields()) {
      String name = f.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      Object data = uncheck(() -> f.get(parentData));
      if (data.getClass().isAnnotationPresent(BuiltinAggregate.class)) {
        final String key = path + "/" + name;
        final ROPair<MortarDataType, Object> value = autogenBuiltinAggregate(working, data, key);
        fields.add(new ROPair<>(name, value.first.newTupleField(values.size())));
        values.add(value.second);
      } else {
        working.registerSingletonBuiltinExportable(data);
        if (data == NullValue.value) {
          fields.add(new ROPair<>(name, NullFieldAll.inst));
          values.add(null);
        } else {
          final MortarObjectImplType type = working.autogenMortarObjectType(data.getClass());
          fields.add(new ROPair<>(name, type.newTupleField(values.size())));
          values.add(data);
        }
      }
    }
    for (Method m : parentData.getClass().getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) {
        continue;
      }
      String name = m.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      final ROPair<MortarRecordFieldable, FuncInfo> mObj =
          autoMortarHalfStaticMethodType(working, parentData.getClass(), m.getName());
      fields.add(new ROPair<>(name, mObj.first.newTupleField(values.size())));
      values.add(mObj.second);
    }
    final MortarRecordType outType = new MortarRecordType(fields);
    working.registerSingletonBuiltinExportable(outType);
    final Object[] outValue = values.toArray(Object[]::new);
    working.registerSingletonBuiltinExportable(outValue);
    return new ROPair<>(outType, outValue);
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
    public final TSMap<Class, Exporter> detachedExportableTypeLookup = new TSMap<>();
    public final TSMap<Class, MortarObjectImplType> autoMortarHalfDataTypes = new TSMap<>();
    public final TSMap<Class, Exporter> autoExportableTypeLookup = new TSMap<>();
    public final TSMap<Class, InlineType> inlineTypeLookup = new TSMap<>();
    public final TSMap<ObjId<Object>, Integer> singletonBuiltinKeyLookup = new TSMap<>();
    public final TSList<Object> singletonBuiltinLookup = new TSList<>();
    public final TSMap<Class, MortarDataType> primitivePrototypeLookup = new TSMap<>();

    private void registerSingletonBuiltinExportable(Object e) {
      singletonBuiltinKeyLookup.put(new ObjId<>(e), singletonBuiltinLookup.size());
      singletonBuiltinLookup.add(e);
    }

    private void autogenExporter(Class klass) {
      final BuiltinAutoExporter type = new BuiltinAutoExporter(klass);
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
      registerSingletonBuiltinExportable(type);
    }

    public MortarObjectImplType autogenMortarObjectType(Class klass) {
      MortarObjectImplType out = autoMortarHalfDataTypes.getOpt(klass);
      if (out == null) {
        TSMap<Object, MortarObjectField> fields = new TSMap<>();
        TSList<MortarObjectInnerType> inherits = new TSList<>();
        final MortarObjectInnerType innerType =
            new MortarObjectInnerType(JavaBytecodeUtils.qualifiedNameFromClass(klass), inherits);
        MortarObjectImplType out1 = new MortarObjectImplType(innerType, fields);
        autoMortarHalfDataTypes.put(klass, out1);
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
        /*
        for (Field field : klass.getDeclaredFields()) {
          MortarDataType dataType = dataDescriptor(this, field.getType());
          String fieldName = field.getName();
          fields.putNew(fieldName, dataDescriptor( this, field.getType()).type_newField(innerType, fieldName));
        }
         */
        if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
          inherits.add(autogenMortarObjectType(klass.getSuperclass()).meta);
        }
        for (Class iface : klass.getInterfaces()) {
          inherits.add(autogenMortarObjectType(iface).meta);
        }
        out = out1;
      }
      return out;
    }

    public void detachedExportType(Class klass, InlineType inlineType) {
      final DetachedExporter t = new DetachedExporter(TypeInfo.fromClass(klass), inlineType);
      detachedExportableTypeLookup.put(klass, t);
      registerSingletonBuiltinExportable(t);
    }
  }

  public static class DetachedExporter implements Exporter, BuiltinAutoExportable {
    private final InlineType inlineType;
    private final TypeInfo type;

    public DetachedExporter(TypeInfo type, InlineType inlineType) {
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
