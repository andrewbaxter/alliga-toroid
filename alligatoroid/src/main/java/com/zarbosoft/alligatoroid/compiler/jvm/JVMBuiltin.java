package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
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
              .put("array", EvaluateResult.pure(wrapFunction(JVMBuiltin.class, "builtinArray"))));

  public static JVMArrayType builtinArray(Value elementType) {
    return new JVMArrayType((JVMDataType) elementType);
  }

  public static RetClass builtinNewClass(String qualifiedName) {
    JVMClassType type = new JVMClassType(qualifiedName);
    return new RetClass(type, new JVMClassBuilder(type));
  }

  public static RetExternClass builtinExternClass(String qualifiedName) {
    JVMExternClassType type = new JVMExternClassType(qualifiedName);
    return new RetExternClass(type, new JVMExternClassBuilder(type));
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
    public final JVMExternClassBuilder builder;

    public RetExternClass(JVMExternClassType type, JVMExternClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }
}
