package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMArrayType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMByteType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMCharType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMDoubleType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMExternClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMFloatType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMLongType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;

@Meta.Aggregate
public class JVMBuiltin {
  public static final JVMStringType string = JVMStringType.type;
  public static final JVMIntType _int = JVMIntType.type;
  public static final JVMByteType _byte = JVMByteType.value;
  public static final JVMCharType _char = JVMCharType.value;
  public static final JVMDoubleType _double = JVMDoubleType.value;
  public static final JVMFloatType _float = JVMFloatType.value;
  public static final JVMLongType _long = JVMLongType.value;
  public static final JVMBoolType bool = JVMBoolType.type;

  public static JVMArrayType array(JVMType elementType) {
    return JVMArrayType.create(elementType);
  }

  public static RetClass newClass(String qualifiedName) {
    JVMClassInstanceType type =
        JVMClassInstanceType.blank(JVMSharedNormalName.fromString(qualifiedName));
    return new RetClass(type, new JVMConcreteClassBuilder(type));
  }

  public static RetExternClass externClass(String qualifiedName) {
    final JVMExternClassInstanceType type =
        JVMExternClassInstanceType.blank(JVMSharedNormalName.fromString(qualifiedName));
    return new RetExternClass(type, new JVMExternClassBuilder(type));
  }

  public static JVMSoftTypeArray deferredArray(Object object) {
    return JVMSoftTypeArray.create(
        JVMExternClassBuilder.convertType(object));
  }

  public static class RetExternClass {
    public final JVMExternClassInstanceType type;
    public final JVMExternClassBuilder builder;

    public RetExternClass(JVMExternClassInstanceType type, JVMExternClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }

  public static class RetClass {
    public final JVMClassInstanceType type;
    public final JVMConcreteClassBuilder builder;

    public RetClass(JVMClassInstanceType type, JVMConcreteClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }
}
