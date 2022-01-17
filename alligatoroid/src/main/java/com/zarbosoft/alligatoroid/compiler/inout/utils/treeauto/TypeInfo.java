package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeInfo {
  public final Class klass;
  public final TypeInfo[] genericArgs;

  private TypeInfo(Class klass, TypeInfo[] genericArgs) {
    this.klass = klass;
    this.genericArgs = genericArgs;
  }

  public static TypeInfo fromParam(Parameter parameter) {
    Type pt = parameter.getParameterizedType();
    if (pt == null) return new TypeInfo(parameter.getType(), null);
    return fromType(pt);
  }

  public static TypeInfo fromType(Type type) {
    if (type instanceof ParameterizedType) {
      final Type[] genericArgs = ((ParameterizedType) type).getActualTypeArguments();
      TypeInfo[] genericArgs1 = new TypeInfo[genericArgs.length];
      for (int i = 0; i < genericArgs.length; i++) {
        genericArgs1[i] = TypeInfo.fromType(genericArgs[i]);
      }
      return new TypeInfo((Class) ((ParameterizedType) type).getRawType(), genericArgs1);
    } else {
      return new TypeInfo((Class) type, null);
    }
  }

  public static TypeInfo fromClass(Class klass) {
    return new TypeInfo(klass, null);
  }
}
