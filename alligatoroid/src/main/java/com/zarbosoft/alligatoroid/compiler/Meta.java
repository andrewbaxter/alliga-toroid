package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMExternClassBuilder;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfExternClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMPseudoStaticField;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
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
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarArrayType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarByteType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.StaticMethodValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class Meta {
  // Exportable, lowerable, deserializable
  public static final Class[] LANGUAGE = {
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
  };
  // Exportable, lowerable
  public static final Class[] OTHER_AUTO_GRAPH = {
    BundleValue.class,
    JVMHalfExternClassType.class,
    Location.class,
    LocalModuleId.class,
    RemoteModuleId.class,
    BundleModuleSubId.class,
    ImportId.class,
    JVMPseudoStaticField.class,
  };

  // Lowerable, not exportable
  public static final Class[] AUTO_VALUE = {
    JVMExternClassBuilder.class,
  };
  /** Initialized statically, never modified after (thread safe for reads). */
  public static TSMap<Class, MortarAutoObjectType> autoMortarHalfDataTypes = new TSMap<>();

  static {
    for (Class klass : AUTO_VALUE) {
      autoMortarHalfDataType(klass);
    }
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
   * @param method
   * @return
   */
  public static FuncInfo funcDescriptor(Method method) {
    boolean needsModule = false;
    JVMSharedDataDescriptor[] argDescriptor =
        new JVMSharedDataDescriptor[method.getParameters().length];
    for (int i = 0; i < method.getParameters().length; ++i) {
      Parameter parameter = method.getParameters()[i];
      if (i == 0 && parameter.getType() == ModuleCompileContext.class) {
        needsModule = true;
      }
      ROPair<JVMSharedDataDescriptor, MortarDataType> paramDesc =
          dataDescriptor(parameter.getType());
      argDescriptor[i] = paramDesc.first;
    }

    ROPair<JVMSharedDataDescriptor, MortarDataType> retDesc =
        dataDescriptor(method.getReturnType());

    return new FuncInfo(
        method,
        JVMSharedFuncDescriptor.fromParts(retDesc.first, argDescriptor),
        retDesc.second,
        needsModule);
  }

  /**
   * Must be called during initialization (single thread)!
   *
   * @param klass
   * @return
   */
  public static ROPair<JVMSharedDataDescriptor, MortarDataType> dataDescriptor(Class klass) {
    if (klass == void.class) {
      return new ROPair<>(JVMSharedDataDescriptor.VOID, null);
    } else if (klass == String.class) {
      return new ROPair<>(JVMSharedDataDescriptor.STRING, MortarStringType.type);
    } else if (klass == byte[].class) {
      return new ROPair<>(
          JVMSharedDataDescriptor.BYTE_ARRAY, new MortarArrayType(MortarByteType.type));
    } else {
      return new ROPair<>(JVMSharedDataDescriptor.fromClass(klass), autoMortarHalfDataType(klass));
    }
  }

  public static MortarAutoObjectType autoMortarHalfDataType(Class klass) {
    /*
    if (klass == Value.class) {
      throw new Assertion();
    }
     */
    MortarAutoObjectType out = autoMortarHalfDataTypes.getOpt(klass);
    JVMSharedJVMName jvmName = JVMSharedJVMName.fromClass(klass);
    if (out == null) {
      out = new MortarAutoObjectType(jvmName, VariableDataStackValue.class.isAssignableFrom(klass));
      autoMortarHalfDataTypes.put(klass, out);
      TSMap<Object, MortarFieldType> fields = new TSMap<>();
      if (klass != VariableDataStackValue.class)
        for (Method method : klass.getDeclaredMethods()) {
          if (!Modifier.isPublic(method.getModifiers())) continue;
          if (!method.isAnnotationPresent(WrapExpose.class)) continue;
          fields.putNew(method.getName(), new MortarMethodFieldType(funcDescriptor(method)));
        }
      for (Field field : klass.getDeclaredFields()) {
        ROPair<JVMSharedDataDescriptor, MortarDataType> desc = dataDescriptor(field.getType());
        MortarDataType dataType = desc.second;
        String fieldName = field.getName();
        fields.putNew(fieldName, new MortarDataFieldType(field, dataType));
      }
      out.fields = fields;
    }
    return out;
  }

  public static StaticMethodValue autoMortarHalfStaticMethodType(Class klass, String name) {
    Method method = null;
    for (Method checkMethod : klass.getMethods()) {
      if (!checkMethod.getName().equals(name)) continue;
      method = checkMethod;
      break;
    }
    if (method == null)
      throw Assertion.format("builtin wrap [%s] function [%s] missing", klass.getName(), name);
    return new StaticMethodValue(funcDescriptor(method));
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface WrapExpose {}

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
