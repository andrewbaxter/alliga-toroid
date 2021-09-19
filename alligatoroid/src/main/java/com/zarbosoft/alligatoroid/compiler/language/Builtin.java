package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMBuiltin;
import com.zarbosoft.alligatoroid.compiler.jvm.MultiError;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.CreatedFile;
import com.zarbosoft.alligatoroid.compiler.mortar.Function;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarClass;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfArrayType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfByteType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Builtin extends LanguageValue {
  public static TSMap<Class, MortarClass> wrappedClasses = new TSMap<>();
  public static LooseRecord builtin =
      new LooseRecord(
          new TSOrderedMap()
              .put("log", EvaluateResult.pure(wrapFunction(Builtin.class, "builtinLog")))
              .put("jvm", EvaluateResult.pure(JVMBuiltin.builtin))
              .put("null", EvaluateResult.pure(NullValue.value))
              .put("nullType", EvaluateResult.pure(NullType.type))
              .put(
                  "createFile",
                  EvaluateResult.pure(wrapFunction(Builtin.class, "builtinCreateFile"))));

  public Builtin(Location id) {
    super(id, false);
  }

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

  public static void builtinLog(Module module, String message) {
    module.log.log.add(message);
  }

  public static FuncInfo funcDescriptor(Method method) {
    boolean needsModule = false;
    String[] argDescriptor = new String[method.getParameters().length];
    for (int i = 0; i < method.getParameters().length; ++i) {
      Parameter parameter = method.getParameters()[i];
      if (i == 0 && parameter.getType() == Module.class) {
        needsModule = true;
      }
      ROPair<String, MortarHalfDataType> paramDesc = dataDescriptor(parameter.getType());
      argDescriptor[i] = paramDesc.first;
    }

    ROPair<String, MortarHalfDataType> retDesc = dataDescriptor(method.getReturnType());

    return new FuncInfo(
        JVMDescriptor.func(retDesc.first, argDescriptor), retDesc.second, needsModule);
  }

  public static ROPair<String, MortarHalfDataType> dataDescriptor(Class klass) {
    if (klass == void.class) {
      return new ROPair<>(JVMDescriptor.voidDescriptor(), null);
    } else if (klass == String.class) {
      return new ROPair<>(
          JVMDescriptor.objDescriptorFromReal(String.class), MortarHalfStringType.type);
    } else if (klass == byte[].class) {
      return new ROPair<>(
          JVMDescriptor.arrayDescriptor(JVMDescriptor.byteDescriptor()),
          new MortarHalfArrayType(MortarHalfByteType.type));
    } else {
      return new ROPair<>(JVMDescriptor.objDescriptorFromReal(klass), wrapClass(klass));
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface WrapExpose {}

  public static MortarHalfDataType wrapClass(Class klass) {
    /*
    if (klass == Value.class) {
      throw new Assertion();
    }
     */
    MortarClass out = wrappedClasses.getOpt(klass);
    if (out == null) {
      out = new MortarClass(JVMDescriptor.jvmName(klass));
      wrappedClasses.put(klass, out);
      TSMap<Object, MortarHalfType> fields = new TSMap<>();
      if (klass != Value.class)
        for (Method method : klass.getDeclaredMethods()) {
          if (!Modifier.isPublic(method.getModifiers())) continue;
          if (!method.isAnnotationPresent(WrapExpose.class)) continue;
          FuncInfo info = funcDescriptor(method);
          fields.putNew(
              method.getName(),
              new MortarMethodFieldType(
                  out, method.getName(), info.descriptor, info.returnType, info.needsModule));
        }
      /*
      TODO
      for (Field field : klass.getDeclaredFields()) {
        ROPair<String, MortarHalfType> desc = dataDescriptor(field.getType());
        fields.putNew()
      }
       */
      out.fields = fields;
    }
    return out;
  }

  public static Function wrapFunction(Class klass, String name) {
    Method method = null;
    for (Method checkMethod : klass.getMethods()) {
      if (!checkMethod.getName().equals(name)) continue;
      method = checkMethod;
      break;
    }
    FuncInfo info = funcDescriptor(method);
    return new Function(
        JVMDescriptor.jvmName(klass.getCanonicalName()), name, info.descriptor, info.returnType);
  }

  public static CreatedFile builtinCreateFile(String path) {
    return new CreatedFile(path);
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    return EvaluateResult.pure(builtin);
  }

  public static class FuncInfo {
    public final String descriptor;
    public final MortarHalfDataType returnType;
    public final boolean needsModule;

    public FuncInfo(String descriptor, MortarHalfDataType returnType, boolean needsModule) {
      this.descriptor = descriptor;
      this.returnType = returnType;
      this.needsModule = needsModule;
    }
  }
}
