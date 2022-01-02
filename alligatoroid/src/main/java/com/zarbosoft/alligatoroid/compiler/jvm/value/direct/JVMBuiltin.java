package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMArrayType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMByteType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMCharType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMDoubleType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMFloatType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMLongType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMExternClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;

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
    JVMClassType type = JVMClassType.blank(qualifiedName);
    return new RetClass(type, new JVMConcreteClassBuilder(type));
  }

  public static JVMExternClassType externClass(String qualifiedName, Value setup) {
    return new JVMExternClassType(qualifiedName, setup);
  }

  public static class RetClass {
    public final JVMClassType type;
    public final JVMConcreteClassBuilder builder;

    public RetClass(JVMClassType type, JVMConcreteClassBuilder builder) {
      this.type = type;
      this.builder = builder;
    }
  }
}
