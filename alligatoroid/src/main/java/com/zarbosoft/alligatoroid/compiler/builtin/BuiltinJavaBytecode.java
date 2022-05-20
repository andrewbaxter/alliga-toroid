package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaQualifiedName;
import com.zarbosoft.rendaw.common.ROList;

@Meta.BuiltinAggregate
public class BuiltinJavaBytecode {
  public static final BuiltinJavaBytecodeCode code = null;
  public static final BuiltinJavaBytecodeDescriptors desc = null;

  public static JavaBytecodeBindingKey bindingKey() {
    return new JavaBytecodeBindingKey();
  }

  public static JavaQualifiedName qualifiedName(String name) {
    return JavaBytecodeUtils.qualifiedName(name);
  }

  public static JavaInternalName internalName(String name) {
    return JavaBytecodeUtils.internalName(name);
  }

  public static JavaMethodDescriptor methodDescriptor(
      JavaDataDescriptor returnType, ROList<JavaDataDescriptor> argumentTypes) {
    return JavaMethodDescriptor.fromParts(returnType, argumentTypes);
  }

  public static JavaClass _class(JavaInternalName name) {
    return new JavaClass(name);
  }
}
