package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseRecord;
import com.zarbosoft.rendaw.common.TSOrderedMap;

import static com.zarbosoft.alligatoroid.compiler.language.Builtin.wrapFunction;

public class JVMBuiltin {
  public static final LooseRecord builtin =
      new LooseRecord(
          new TSOrderedMap()
              .put(
                  "newClass",
                  EvaluateResult.pure(wrapFunction(JVMBuiltin.class, "builtinNewClass")))
              .put(
                  "externClass",
                  EvaluateResult.pure(wrapFunction(JVMBuiltin.class, "builtinExternClass")))
              .put(
                  "externStaticField",
                  EvaluateResult.pure(wrapFunction(JVMBuiltin.class, "builtinExternStaticField")))
              .put("string", EvaluateResult.pure(JVMStringType.value))
              .put("int", EvaluateResult.pure(JVMIntType.value))
              .put("byte", EvaluateResult.pure(JVMByteType.value))
              .put("char", EvaluateResult.pure(JVMCharType.value))
              .put("double", EvaluateResult.pure(JVMDoubleType.value))
              .put("float", EvaluateResult.pure(JVMFloatType.value))
              .put("long", EvaluateResult.pure(JVMLongType.value))
              .put("bool", EvaluateResult.pure(JVMBoolType.value))
              .put("array", EvaluateResult.pure(wrapFunction(JVMBuiltin.class, "builtinArray"))));

  public static JVMArrayType builtinArray(Value elementType) {
    return new JVMArrayType((JVMDataType) elementType);
  }

  public static RetClass builtinNewClass(String qualifiedName) {
    JVMClassType type = new JVMClassType(qualifiedName);
    return new RetClass(type, new JVMClassBuilder(type));
  }
  public static RetExternClass builtinExternClass(String qualifiedName, Value setup) {
    JVMExternClassType type = new JVMExternClassType(qualifiedName, setup);
    JVMExternConstructor constructor = new JVMExternConstructor(type);
    return new RetExternClass(type, constructor);
  }

  public static Value builtinExternStaticField(
      String qualifiedClassName, String fieldName, Value spec) {
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
