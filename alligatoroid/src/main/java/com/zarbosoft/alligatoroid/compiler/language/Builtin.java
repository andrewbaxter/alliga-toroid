package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.Function;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarClass;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfArrayType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfByteType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarHalfType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import static com.zarbosoft.rendaw.common.Common.uncheck;
import static org.objectweb.asm.Opcodes.GETFIELD;

public class Builtin extends LanguageValue {
  /** Initialized statically, never modified after (thread safe for reads). */
  public static TSMap<Class, MortarClass> wrappedClasses = new TSMap<>();

  public static LooseRecord builtin =
      aggregateBuiltin(com.zarbosoft.alligatoroid.compiler.mortar.Builtin.class);

  public Builtin(Location id) {
    super(id, false);
  }

  private static LooseRecord aggregateBuiltin(Class klass) {
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
        values.put(name, EvaluateResult.pure(aggregateBuiltin(data.getClass())));
      }
    }
    for (Method m : klass.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) continue;
      String name = m.getName();
      if (name.startsWith("_")) {
        name = name.substring(1);
      }
      values.put(name, EvaluateResult.pure(wrapFunction(klass, m.getName())));
    }
    return new LooseRecord(values);
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
      return new ROPair<>(JVMDescriptor.VOID_DESCRIPTOR, null);
    } else if (klass == String.class) {
      return new ROPair<>(
          JVMDescriptor.objDescriptorFromReal(String.class), MortarHalfStringType.type);
    } else if (klass == byte[].class) {
      return new ROPair<>(
          JVMDescriptor.arrayDescriptor(JVMDescriptor.BYTE_DESCRIPTOR),
          new MortarHalfArrayType(MortarHalfByteType.type));
    } else {
      return new ROPair<>(JVMDescriptor.objDescriptorFromReal(klass), wrapClass(klass));
    }
  }

  public static MortarHalfDataType wrapClass(Class klass) {
    /*
    if (klass == Value.class) {
      throw new Assertion();
    }
     */
    MortarClass out = wrappedClasses.getOpt(klass);
    String jvmName = JVMDescriptor.jvmName(klass);
    if (out == null) {
      out = new MortarClass(jvmName);
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
      for (Field field : klass.getDeclaredFields()) {
        ROPair<String, MortarHalfDataType> desc = dataDescriptor(field.getType());
        MortarHalfDataType dataType = desc.second;
        String fieldName = field.getName();
        fields.putNew(
            fieldName,
            new MortarHalfType() {
              @Override
              public Value asValue(MortarProtocode lower) {
                return dataType.asValue(
                    new MortarProtocode() {
                      @Override
                      public MortarCode lower() {
                        return (MortarCode)
                            new MortarCode()
                                .add(lower.lower())
                                .add(
                                    new FieldInsnNode(
                                        GETFIELD, jvmName, fieldName, dataType.jvmDesc()));
                      }

                      @Override
                      public TargetCode drop(Context context, Location location) {
                        return null;
                      }
                    });
              }
            });
      }
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
    if (method == null)
      throw Assertion.format("builtin wrap [%s] function [%s] missing", klass.getName(), name);
    FuncInfo info = funcDescriptor(method);
    return new Function(
        JVMDescriptor.jvmName(klass.getCanonicalName()), name, info.descriptor, info.returnType);
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    return EvaluateResult.pure(builtin);
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface WrapExpose {}

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
