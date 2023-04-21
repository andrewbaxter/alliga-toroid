package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;

public class JavaBytecodeUtils {
  public static boolean empty(JavaBytecode e) {
    if (e == null) {
      return true;
    }
    if (e instanceof JavaBytecodeSequence && ((JavaBytecodeSequence) e).size() == 0) {
      return true;
    }
    return false;
  }

  public static void print(MethodNode m) {
    // FIXME! DEBUG
    System.out.format("--\n");
    Textifier printer = new Textifier();
    m.accept(new TraceMethodVisitor(printer));
    PrintWriter printWriter = new PrintWriter(System.out);
    printer.print(printWriter);
    printWriter.flush();
    // FIXME! DEBUG
  }

  public static JavaBytecode instantiate(
      int location, JavaInternalName klass, JavaMethodDescriptor desc, JavaBytecode arguments) {
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(new JavaBytecodeInstructionObj(new TypeInsnNode(NEW, klass.value)));
    code.add(Global.JBC_DUP);
    code.add(arguments);
    code.add(
        new JavaBytecodeInstructionObj(
            new MethodInsnNode(INVOKESPECIAL, klass.value, "<init>", desc.value, false)));
    return code;
  }

  public static JavaBytecode accessField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new FieldInsnNode(GETFIELD, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode setField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new FieldInsnNode(PUTFIELD, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode setStaticField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new FieldInsnNode(PUTSTATIC, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode accessStaticField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new FieldInsnNode(GETSTATIC, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode callConstructor(
      int location, JavaInternalName klass, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new MethodInsnNode(INVOKESPECIAL, klass.value, "<init>", methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callInterfaceMthod(
      int location, JavaInternalName klass, String method, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new MethodInsnNode(INVOKEINTERFACE, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callMethod(
      int location, JavaInternalName klass, String method, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new MethodInsnNode(INVOKEVIRTUAL, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callStaticMethodReflect(Class klass, String method) {
    for (Method method1 : klass.getMethods()) {
      if (!method.equals(method1.getName())) {
        continue;
      }
      JavaDataDescriptor ret = JavaDataDescriptor.fromClass(method1.getReturnType());
      JavaDataDescriptor[] args = new JavaDataDescriptor[method1.getParameterCount()];
      for (int i = 0; i < method1.getParameters().length; i++) {
        args[i] = JavaDataDescriptor.fromClass(method1.getParameters()[i].getType());
      }
      return new JavaBytecodeInstructionObj(
          new MethodInsnNode(
              INVOKESTATIC,
              internalNameFromClass(klass).value,
              method,
              JavaMethodDescriptor.fromParts(ret, new TSList<>(args)).value,
              false));
    }
    throw new Assertion();
  }

  public static JavaBytecode callStaticMethod(
      int location, JavaInternalName klass, String method, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) {
      code.add(JavaBytecodeLineNumber.create(location));
    }
    code.add(
        new JavaBytecodeInstructionObj(
            new MethodInsnNode(INVOKESTATIC, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode literalString(String value) {
    return new JavaBytecodeInstructionObj(new LdcInsnNode(value));
  }

  public static JavaBytecode inst(int opcode) {
    return new JavaBytecodeInstructionInt(opcode);
  }

  public static JavaBytecode literalLong(long value) {
    if (value == 0) {
      return Global.JBC_literalLong0;
    }
    if (value == 1) {
      return Global.JBC_literalLong1;
    }
    return new JavaBytecodeInstructionObj(new LdcInsnNode(value));
  }

  public static JavaBytecode literalBool(boolean value) {
    return literalIntShortByte(value ? 1 : 0);
  }

  public static JavaBytecode literalIntShortByte(int value) {
    switch (value) {
      case -1:
        return Global.JBC_literalIntM1;
      case 0:
        return Global.JBC_literalInt0;
      case 1:
        return Global.JBC_literalInt1;
      case 2:
        return Global.JBC_literalInt2;
      case 3:
        return Global.JBC_literalInt3;
      case 4:
        return Global.JBC_literalInt4;
      case 5:
        return Global.JBC_literalInt5;
    }
    return new JavaBytecodeInstructionObj(new LdcInsnNode(value));
  }

  public static JavaBytecode box(JavaDataDescriptor primDescriptor, JavaInternalName box) {
    return callStaticMethod(
        -1,
        box,
        "valueOf",
        JavaMethodDescriptor.fromParts(
            JavaDataDescriptor.fromJVMName(box), new TSList<>(primDescriptor)));
  }

  public static JavaBytecode cast(JavaDataDescriptor toClass) {
    return new JavaBytecodeInstructionObj(new TypeInsnNode(CHECKCAST, toClass.value));
  }

  public static JavaBytecode bool_(boolean value) {
    return inst(value ? ICONST_1 : ICONST_0);
  }

  public static JavaQualifiedName qualifiedNameFromClass(Class klass) {
    final JavaQualifiedName out = new JavaQualifiedName();
    out.value = new TSList<>();
    Class prevAt = klass;
    Class at = klass.getNestHost();
    while (true) {
      out.value.add(at.getSimpleName());
      if (at == prevAt) {
          out.value.removeLast();
        out.value.add(at.getCanonicalName());
        break;
      }
      prevAt = at;
      at = at.getNestHost();
    }
    out.value.reverse();
    out.postInit();
    return out;
  }

  public static JavaQualifiedName qualifiedName(String name) {
    final JavaQualifiedName out = new JavaQualifiedName();
    out.value = new TSList<>();
    out.value.add(name);
    out.postInit();
    return out;
  }

  public static JavaInternalName internalName(String name) {
    final JavaInternalName out = new JavaInternalName();
    out.value = name;
    out.postInit();
    return out;
  }

  public static JavaBytecode loadIntShortByteBool(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ILOAD, key);
  }

  public static JavaBytecode storeIntShortByteBool(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ISTORE, key);
  }

  public static JavaBytecode loadLong(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(LLOAD, key);
  }

  public static JavaBytecode storeLong(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(LSTORE, key);
  }

  public static JavaBytecode loadFloat(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(FLOAD, key);
  }

  public static JavaBytecode storeFloat(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(FSTORE, key);
  }

  public static JavaBytecode loadDouble(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(DLOAD, key);
  }

  public static JavaBytecode storeDouble(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(DSTORE, key);
  }

  public static JavaBytecode loadObj(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ALOAD, key);
  }

  public static JavaBytecode storeObj(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ASTORE, key);
  }

  public static JavaInternalName internalNameFromClass(Class klass) {
    return qualifiedNameFromClass(klass).asInternalName();
  }

  public static JavaBytecodeSequence seq() {
    final JavaBytecodeSequence out = new JavaBytecodeSequence();
    out.postInit();
    return out;
  }
}
