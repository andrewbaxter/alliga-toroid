package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.PrimitiveExportType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMExternClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMConstructor;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvm.mortartypes.JVMClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.mortartypes.JVMConstructorType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMPseudoStaticFieldValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
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
import com.zarbosoft.alligatoroid.compiler.model.language.Import;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.model.language.Local;
import com.zarbosoft.alligatoroid.compiler.model.language.Lower;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.model.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.model.language.Stage;
import com.zarbosoft.alligatoroid.compiler.model.language.Tuple;
import com.zarbosoft.alligatoroid.compiler.model.language.Wrap;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.StaticMethodMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarArrayType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarBoolType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarByteType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarIntType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStaticMethodType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.Assertion;
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

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin.nullType;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Meta {
  public static final ROMap<Exportable, String> builtinToSemiKey;
  public static final ROMap<String, Exportable> semiKeyToBuiltin;
  public static final ROMap<Class, ExportableType> autoBuiltinExportTypes;
  public static final ROMap<MortarDataType, PrimitiveExportType> primitiveMortarTypeToExportType;
  public static final ROMap<Class, PrimitiveExportType> primitiveTypeToExportType;
  public static final ROMap<String, PrimitiveExportType> primitiveKeyToExportType;
  public static ROMap<Class, MortarDataType> autoMortarHalfDataTypes;
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

    for (ROPair<Class[], MortarDataType> pair :
        new ROPair[] {
          new ROPair<>(new Class[] {JVMConstructor.class}, JVMConstructorType.type),
          new ROPair<>(
              new Class[] {JVMClassInstanceType.class, JVMExternClassInstanceType.class},
              JVMClassType.type),
          new ROPair<>(new Class[] {StaticMethodMeta.class}, MortarStaticMethodType.type),
        }) {
      for (Class klass : pair.first) {
        working.mortarType(klass, pair.second);
      }
      working.singletonExportable((SingletonBuiltinExportable) pair.second);
    }
    working.generateMortarType(ModuleId.class);
    working.generateMortarType(JVMExternClassBuilder.class);
    {
      // working.singletonExportable(nullValue);
      // working.mortarType(nullValue.getClass(), nullValue.type);
      // working.singletonExportable(nullValue.type);
    }
    working.singletonExportable(ConstExportType.exportType);
    for (Class<AutoBuiltinExportable> languageElement :
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
          Import.class,
          Wrap.class,
        }) {
      working.nonSingletonExportable(languageElement);
      working.generateMortarType(languageElement);
    }
    for (Class klass :
        new Class[] {
          JVMExternClassInstanceType.class,
          BundleValue.class,
          Location.class,
          LocalModuleId.class,
          RemoteModuleId.class,
          BundleModuleSubId.class,
          ImportId.class,
          JVMSharedNormalName.class,
          JVMSharedJVMName.class,
        }) {
        if (NoExportValue.class.isAssignableFrom(klass)) throw new Assertion();
        if (!AutoBuiltinExportable.class.isAssignableFrom(klass)) throw new Assertion();
      working.nonSingletonExportable(klass);
      working.generateMortarType(klass);
    }

    Meta.builtin = Meta.aggregateBuiltinForGraph(working, MortarBuiltin.class, "");

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
    boolean needsModule = false;
    JVMSharedDataDescriptor[] argDescriptor =
        new JVMSharedDataDescriptor[method.getParameters().length];
    for (int i = 0; i < method.getParameters().length; ++i) {
      Parameter parameter = method.getParameters()[i];
      if (i == 0 && parameter.getType() == ModuleCompileContext.class) {
        needsModule = true;
      }
      ROPair<JVMSharedDataDescriptor, MortarDataType> paramDesc =
          dataDescriptor(working, parameter.getType());
      argDescriptor[i] = paramDesc.first;
    }

    ROPair<JVMSharedDataDescriptor, MortarDataType> retDesc =
        dataDescriptor(working, method.getReturnType());

    return new FuncInfo(
        method,
        JVMSharedFuncDescriptor.fromParts(retDesc.first, argDescriptor),
        retDesc.second,
        needsModule);
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
  public static ROPair<JVMSharedDataDescriptor, MortarDataType> dataDescriptor(
      WorkingMeta working, Class klass) {
    if (klass == void.class) {
      return new ROPair<>(JVMSharedDataDescriptor.VOID, nullType);
    } else if (klass == String.class) {
      return new ROPair<>(JVMSharedDataDescriptor.STRING, MortarStringType.type);
    } else if (klass == byte[].class) {
      return new ROPair<>(
          JVMSharedDataDescriptor.BYTE_ARRAY, new MortarArrayType(MortarByteType.type));
    } else {
      return new ROPair<>(
          JVMSharedDataDescriptor.fromObjectClass(klass), working.generateMortarType(klass));
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
    return MortarStaticMethodType.type.constAsValue(
        new StaticMethodMeta(funcDescriptor(working, method)));
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
      if (data.getClass().isAnnotationPresent(Aggregate.class)) {
        final String key = path + "/" + name;
        final LooseRecord value = aggregateBuiltinForGraph(working, data.getClass(), key);
        values.put(name, EvaluateResult.pure(value));
        working.builtinToSemiKey.put(value, key);
        working.semiKeyToBuiltin.put(key, value);
      } else {
        working.singletonExportable((SingletonBuiltinExportable) data);
        working.generateMortarType(data.getClass());
        if (data instanceof VariableDataStackValue) {
          values.put(name, EvaluateResult.pure((VariableDataStackValue) data));
        } else {
          values.put(
              name,
              EvaluateResult.pure(
                  working.generateMortarType(data.getClass()).constBuiltinSingletonAsValue(data)));
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
  public @interface Aggregate {}

  private static class WorkingMeta {
    public final TSMap<Class, MortarDataType> autoMortarHalfDataTypes = new TSMap<>();
    public final TSMap<Class, ExportableType> autoBuiltinExportTypes = new TSMap<>();
    public final TSMap<Exportable, String> builtinToSemiKey = new TSMap<>();
    public final TSMap<String, Exportable> semiKeyToBuiltin = new TSMap<>();
    public int singletonCount;

    public void mortarType(Class klass, MortarDataType type) {
      autoMortarHalfDataTypes.put(klass, type);
    }

    private void singletonExportable(SingletonBuiltinExportable e) {
      final String key = "_singleton_" + singletonCount++;
      singletonExportable(key, e);
    }

    private void singletonExportable(String key, SingletonBuiltinExportable e) {
      builtinToSemiKey.put(e, key);
      semiKeyToBuiltin.put(key, e);
    }

    private void nonSingletonExportable(Class klass) {
      final AutoBuiltinExportableType type = new AutoBuiltinExportableType(klass);
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

    public MortarDataType generateMortarType(Class klass) {
      MortarDataType out = autoMortarHalfDataTypes.getOpt(klass);
      if (out == null) {
        MortarAutoObjectType out1 =
            new MortarAutoObjectType(klass, VariableDataStackValue.class.isAssignableFrom(klass));
        autoMortarHalfDataTypes.put(klass, out1);
        singletonExportable(out1);
        TSMap<Object, MortarFieldType> fields = new TSMap<>();
        if (klass != VariableDataStackValue.class)
          for (Method method : klass.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (!method.isAnnotationPresent(WrapExpose.class)) continue;
            fields.putNew(
                method.getName(), new MortarMethodFieldType(funcDescriptor(this, method)));
          }
        for (Field field : klass.getDeclaredFields()) {
          ROPair<JVMSharedDataDescriptor, MortarDataType> desc =
              dataDescriptor(this, field.getType());
          MortarDataType dataType = desc.second;
          String fieldName = field.getName();
          fields.putNew(fieldName, new MortarDataFieldType(field, dataType));
        }
        out1.fields = fields;
        TSList<MortarDataType> inherits = new TSList<>();
        if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
          inherits.add(generateMortarType(klass.getSuperclass()));
        }
        for (Class iface : klass.getInterfaces()) {
          inherits.add(generateMortarType(iface));
        }
        out1.inherits = inherits;
        out = out1;
      }
      return out;
    }
  }

  public static class FuncInfo {
    public final Method method;
    public final JVMSharedFuncDescriptor descriptor;
    public final MortarDataType returnType;
    public final boolean needsModule;

    public FuncInfo(
        Method method,
        JVMSharedFuncDescriptor descriptor,
        MortarDataType returnType,
        boolean needsModule) {
      this.method = method;
      this.descriptor = descriptor;
      this.returnType = returnType;
      this.needsModule = needsModule;
    }
  }
}
