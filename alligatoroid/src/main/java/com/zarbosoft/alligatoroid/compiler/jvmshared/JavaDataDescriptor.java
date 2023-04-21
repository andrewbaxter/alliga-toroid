package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.rendaw.common.Assertion;

import java.util.Objects;

/** Like La/b/c; */
public class JavaDataDescriptor implements BuiltinAutoExportable {
  @BuiltinAutoExporter.Param public String value;

  public JavaDataDescriptor() {}

  public static JavaDataDescriptor fromJVMName(JavaInternalName name) {
    return create("L" + name + ";");
  }

  public static JavaDataDescriptor fromObjectClass(Class klass) {
    return fromJVMName(JavaBytecodeUtils.internalNameFromClass(klass));
  }

  public static JavaDataDescriptor fromClass(Class t) {
    if (t == int.class) {
      return Global.DESC_INT;
    }
    if (t == byte.class) {
      return Global.DESC_BYTE;
    }
    if (t == char.class) {
      return Global.DESC_CHAR;
    }
    if (t == short.class) {
      return Global.DESC_SHORT;
    }
    if (t == long.class) {
      return Global.DESC_LONG;
    }
    if (t == boolean.class) {
      return Global.DESC_BOOL;
    }
    if (t == float.class) {
      return Global.DESC_FLOAT;
    }
    if (t == double.class) {
      return Global.DESC_DOUBLE;
    }
    if (t.isPrimitive()) {
      throw new Assertion();
    }
    return fromObjectClass(t);
  }

  public static JavaDataDescriptor create(String value) {
    final JavaDataDescriptor out = new JavaDataDescriptor();
    out.value = value;
    return out;
  }

  public String toString() {
    return value;
  }

  public JavaDataDescriptor array() {
    return create("[" + value);
  }

  @Override
  public boolean equals(Object o) {
    return Utils.reflectEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
