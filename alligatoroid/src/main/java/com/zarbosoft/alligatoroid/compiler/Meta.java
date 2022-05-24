package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.builtin.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinArtifactType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.PrimitiveExportType;
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
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinArtifact;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarBoolType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarBytesType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarIntType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
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
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Meta {
  public static final ROMap<Artifact, String> builtinToSemiKey;
  public static final ROMap<String, Artifact> semiKeyToBuiltin;
  public static final ROMap<Class, AutoBuiltinArtifactType> autoBuiltinExportTypes;
  public static final ROMap<MortarDataType, PrimitiveExportType> primitiveMortarTypeToExportType;
  public static final ROMap<Class, PrimitiveExportType> primitiveTypeToExportType;
  public static final ROMap<String, PrimitiveExportType> primitiveKeyToExportType;
  public static ROMap<Class, MortarSimpleDataType> autoMortarHalfDataTypes;
  public static LooseRecord builtin;

  static {
    //// Graph id/primitive type conversions
    // =============================
    /// Prepare type converters for non-value, non-collection types
    TSMap<MortarDataType, PrimitiveExportType> primitiveMortarTypeToExportType0 = new TSMap<>();
    TSMap<Class, PrimitiveExportType> primitiveTypeToExportType0 = new TSMap<>();
    TSMap<String, PrimitiveExportType> primitiveKeyToExportType0 = new TSMap<>();

    PrimitiveExportType intConverter =
        new PrimitiveExportType() {
          @Override
          public String key() {
            return "int";
          }

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
            return SemiserialInt.create((Integer) data);
          }
        };
    primitiveMortarTypeToExportType0.put(MortarIntType.type, intConverter);
    primitiveMortarTypeToExportType0.put(MortarImmutableType.intType, intConverter);
    primitiveTypeToExportType0.put(Integer.class, intConverter);
    primitiveTypeToExportType0.put(int.class, intConverter);

    PrimitiveExportType boolConverter =
        new PrimitiveExportType() {
          @Override
          public String key() {
            return "bool";
          }

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
            return SemiserialBool.create((Boolean) data);
          }
        };
    primitiveMortarTypeToExportType0.put(MortarBoolType.type, boolConverter);
    primitiveMortarTypeToExportType0.put(MortarImmutableType.boolType, boolConverter);
    primitiveTypeToExportType0.put(Boolean.class, boolConverter);
    primitiveTypeToExportType0.put(boolean.class, boolConverter);

    PrimitiveExportType stringConverter =
        new PrimitiveExportType() {
          @Override
          public String key() {
            return "string";
          }

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
            return SemiserialString.create((String) data);
          }
        };
    primitiveMortarTypeToExportType0.put(MortarStringType.type, stringConverter);
    primitiveMortarTypeToExportType0.put(MortarImmutableType.stringType, stringConverter);
    primitiveTypeToExportType0.put(String.class, stringConverter);

    PrimitiveExportType nullConverter =
        new PrimitiveExportType() {
          @Override
          public String key() {
            return "null";
          }

          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleString(SemiserialString s) {
                    return null;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return SemiserialString.create("");
          }
        };
    primitiveMortarTypeToExportType0.put(MortarNullType.type, nullConverter);
    primitiveMortarTypeToExportType0.put(MortarImmutableType.nullType, nullConverter);

    for (Map.Entry<Class, PrimitiveExportType> e : primitiveTypeToExportType0) {
      primitiveKeyToExportType0.putReplace(e.getValue().key(), e.getValue());
    }
    primitiveTypeToExportType = primitiveTypeToExportType0;
    primitiveKeyToExportType = primitiveKeyToExportType0;
    primitiveMortarTypeToExportType = primitiveMortarTypeToExportType0;

    // Non-primitives
    final WorkingMeta working = new WorkingMeta();

    for (ROPair<Class[], MortarSimpleDataType> pair : new ROPair[] {}) {
      for (Class klass : pair.first) {
        working.mortarType(klass, pair.second);
      }
      working.singletonExportable((SingletonBuiltinArtifact) pair.second);
    }
    working.generateMortarType(ModuleId.class);
    {
      // working.singletonExportable(nullValue);
      // working.mortarType(nullValue.getClass(), nullValue.type);
      // working.singletonExportable(nullValue.type);
    }
    working.singletonExportable(ConstExportType.exportType);
    for (Class<AutoBuiltinArtifact> languageElement :
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
      working.nonSingletonExportable(languageElement);
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
      if (!AutoBuiltinArtifact.class.isAssignableFrom(klass)) throw new Assertion();
      working.nonSingletonExportable(klass);
      working.generateMortarType(klass);
    }

    Meta.builtin = Meta.aggregateBuiltinForGraph(working, Builtin.class, "");

    // Done
    builtinToSemiKey = working.builtinToSemiKey;
    semiKeyToBuiltin = working.semiKeyToBuiltin;
    autoBuiltinExportTypes = working.autoBuiltinExportTypes;
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
        working.builtinToSemiKey.put(value, key);
        working.semiKeyToBuiltin.put(key, value);
      } else {
        working.singletonExportable((SingletonBuiltinArtifact) data);
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
    public final TSMap<Class, MortarSimpleDataType> autoMortarHalfDataTypes = new TSMap<>();
    public final TSMap<Class, AutoBuiltinArtifactType> autoBuiltinExportTypes = new TSMap<>();
    public final TSMap<Artifact, String> builtinToSemiKey = new TSMap<>();
    public final TSMap<String, Artifact> semiKeyToBuiltin = new TSMap<>();
    public int singletonCount;

    public void mortarType(Class klass, MortarSimpleDataType type) {
      autoMortarHalfDataTypes.put(klass, type);
    }

    private void singletonExportable(SingletonBuiltinArtifact e) {
      final String key = "_singleton_" + singletonCount++;
      singletonExportable(key, e);
    }

    private void singletonExportable(String key, SingletonBuiltinArtifact e) {
      builtinToSemiKey.put(e, key);
      semiKeyToBuiltin.put(key, e);
    }

    private void nonSingletonExportable(Class klass) {
      final AutoBuiltinArtifactType type = new AutoBuiltinArtifactType(klass);
      autoBuiltinExportTypes.put(klass, type);
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
      singletonExportable(klass.getCanonicalName(), type);
    }

    public MortarSimpleDataType generateMortarType(Class klass) {
      MortarSimpleDataType out = autoMortarHalfDataTypes.getOpt(klass);
      if (out == null) {
        TSMap<Object, MortarObjectFieldType> fields = new TSMap<>();
        TSList<MortarDataType> inherits = new TSList<>();
        MortarObjectType out1 =
            new MortarObjectType(JavaBytecodeUtils.qualifiedNameFromClass(klass), fields, inherits);
        autoMortarHalfDataTypes.put(klass, out1);
        if (klass != VariableDataValue.class)
          for (Method method : klass.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (!method.isAnnotationPresent(WrapExpose.class)) continue;
            fields.putNew(
                method.getName(), new MortarMethodFieldType(funcDescriptor(this, method)));
          }
        for (Field field : klass.getDeclaredFields()) {
          MortarDataType dataType = dataDescriptor(this, field.getType());
          String fieldName = field.getName();
          fields.putNew(fieldName, new MortarSimpleDataType(field, dataType));
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
