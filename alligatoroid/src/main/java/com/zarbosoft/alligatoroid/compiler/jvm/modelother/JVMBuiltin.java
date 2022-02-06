package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfByteType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfCharType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDoubleType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfFloatType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfHalfArrayType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfLongType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfExternClassType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;

@Builtin.Aggregate
public class JVMBuiltin {
  public static final JVMHalfStringType string = JVMHalfStringType.value;
  public static final JVMHalfIntType _int = JVMHalfIntType.type;
  public static final JVMHalfByteType _byte = JVMHalfByteType.value;
  public static final JVMHalfCharType _char = JVMHalfCharType.value;
  public static final JVMHalfDoubleType _double = JVMHalfDoubleType.value;
  public static final JVMHalfFloatType _float = JVMHalfFloatType.value;
  public static final JVMHalfLongType _long = JVMHalfLongType.value;
  public static final JVMHalfBoolType bool = JVMHalfBoolType.type;

  public static JVMHalfHalfArrayType array(VariableDataStackValue elementType) {
    return new JVMHalfHalfArrayType((JVMHalfDataType) elementType);
  }

  public static RetClass newClass(String qualifiedName) {
    JVMHalfClassType type = JVMHalfClassType.blank(JVMSharedNormalName.fromString(qualifiedName));
    return new RetClass(type, new JVMConcreteClassBuilder(type));
  }

  public static JVMHalfExternClassType externClass(String qualifiedName, LanguageElement setup) {
    return JVMHalfExternClassType.blank(JVMSharedNormalName.fromString(qualifiedName), setup);
  }

  public static class RetClass {
    public final JVMHalfClassType type;
    public final JVMConcreteClassBuilder builder;

    public RetClass(JVMHalfClassType type, JVMConcreteClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }
}
