package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.model.Value;

/** Fields in builtin.jvm -- reflected into a value */
public class JVMBuiltin {
  public static final Value string = JVMStringType.value;
  public static final Value _int = JVMIntType.value;
  public static final Value _byte = JVMByteType.value;
  public static final Value _char = JVMCharType.value;
  public static final Value _double = JVMDoubleType.value;
  public static final Value _float = JVMFloatType.value;
  public static final Value _long = JVMLongType.value;
  public static final Value bool = JVMBoolType.value;

  public static JVMArrayType array(Value elementType) {
    return new JVMArrayType((JVMDataType) elementType);
  }

  public static RetClass newClass(String qualifiedName) {
    JVMClassType type = new JVMClassType(qualifiedName);
    return new RetClass(type, new JVMClassBuilder(type));
  }

  public static RetExternClass externClass(String qualifiedName, Value setup) {
    JVMExternClassType type = new JVMExternClassType(qualifiedName, setup);
    JVMExternConstructor constructor = new JVMExternConstructor(type);
    return new RetExternClass(type, constructor);
  }

  public static Value externStaticField(String qualifiedClassName, String fieldName, Value spec) {
    JVMDataType spec1 = (JVMDataType) spec;
    return new JVMExternStaticField(qualifiedClassName, fieldName, spec1);
  }

  public static class RetClass {
    public final JVMClassType type;
    public final JVMClassBuilder builder;

    public RetClass(JVMClassType type, JVMClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }

  public static class RetExternClass {
    public final JVMExternClassType type;
    public final JVMExternConstructor constructor;

    public RetExternClass(JVMExternClassType type, JVMExternConstructor constructor) {
      this.type = type;
      this.constructor = constructor;
    }
  }
}
