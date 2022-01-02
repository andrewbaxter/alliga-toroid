package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Access;
import com.zarbosoft.alligatoroid.compiler.model.language.Bind;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.model.language.Call;
import com.zarbosoft.alligatoroid.compiler.model.language.Import;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.model.language.Local;
import com.zarbosoft.alligatoroid.compiler.model.language.Lower;
import com.zarbosoft.alligatoroid.compiler.model.language.ModLocal;
import com.zarbosoft.alligatoroid.compiler.model.language.ModRemote;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.model.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.model.language.Stage;
import com.zarbosoft.alligatoroid.compiler.model.language.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.AutoBuiltinFunctionType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.AutoBuiltinClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.MortarHalfArrayType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.MortarHalfByteType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.MortarHalfStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.AutoBuiltinMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import static org.objectweb.asm.Opcodes.GETFIELD;

public class Meta {
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
    ModLocal.class,
    ModRemote.class
  };
  public static final Class[] OTHER_AUTO_GRAPH = {
    BundleValue.class,
  };
  /** Initialized statically, never modified after (thread safe for reads). */
  public static TSMap<Class, AutoBuiltinClassType> wrappedClasses = new TSMap<>();

  public static String toUnderscore(Class klass) {
    return toUnderscore(klass.getSimpleName());
  }

  public static String toUnderscore(String name) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < name.length(); ++i) {
      if (Character.isUpperCase(name.codePointAt(i))) {
        if (i > 0) {
          out.append('_');
        }
        out.appendCodePoint(Character.toLowerCase(name.codePointAt(i)));
      } else {
        out.appendCodePoint(name.codePointAt(i));
      }
    }
    return out.toString();
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
      if (i == 0 && parameter.getType() == ModuleCompileContext.class) {
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
    AutoBuiltinClassType out = wrappedClasses.getOpt(klass);
    String jvmName = JVMDescriptor.jvmName(klass);
    if (out == null) {
      out = new AutoBuiltinClassType(jvmName);
      wrappedClasses.put(klass, out);
      TSMap<Object, MortarHalfType> fields = new TSMap<>();
      if (klass != Value.class)
        for (Method method : klass.getDeclaredMethods()) {
          if (!Modifier.isPublic(method.getModifiers())) continue;
          if (!method.isAnnotationPresent(WrapExpose.class)) continue;
          FuncInfo info = funcDescriptor(method);
          fields.putNew(
              method.getName(),
              new AutoBuiltinMethodFieldType(
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
                      public TargetCode drop(EvaluationContext context, Location location) {
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

  public static AutoBuiltinFunctionType wrapFunction(Class klass, String name) {
    Method method = null;
    for (Method checkMethod : klass.getMethods()) {
      if (!checkMethod.getName().equals(name)) continue;
      method = checkMethod;
      break;
    }
    if (method == null)
      throw Assertion.format("builtin wrap [%s] function [%s] missing", klass.getName(), name);
    FuncInfo info = funcDescriptor(method);
    return new AutoBuiltinFunctionType(
        JVMDescriptor.jvmName(klass.getCanonicalName()), name, info.descriptor, info.returnType);
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
