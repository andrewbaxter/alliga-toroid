package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import org.objectweb.asm.Opcodes;
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

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IALOAD;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

public class JavaBytecodeUtils {
  public static final JavaBytecode boxBool = box(JavaDataDescriptor.BOOL, JavaInternalName.BOOL);
  public static final JavaBytecode boxByte = box(JavaDataDescriptor.BYTE, JavaInternalName.BYTE);
  public static final JavaBytecode boxInt = box(JavaDataDescriptor.INT, JavaInternalName.INT);
  public static final JavaBytecode arrayLoadObj = inst(AALOAD);
  public static final JavaBytecode arrayStoreObj = inst(AASTORE);
  public static final JavaBytecode arrayLoadInt = inst(IALOAD);
  public static final JavaBytecode arrayStoreInt = inst(IASTORE);
  public static final JavaBytecode dup = inst(Opcodes.DUP);
  public static final JavaBytecode pop = inst(Opcodes.POP);
  public static final JavaBytecode arrayStoreLong = inst(Opcodes.LASTORE);
  public static final JavaBytecode arrayLoadLong = inst(Opcodes.LALOAD);
  public static final JavaBytecode arrayStoreShort = inst(Opcodes.SASTORE);
  public static final JavaBytecode arrayLoadShort = inst(Opcodes.SALOAD);
  public static final JavaBytecode arrayStoreByteBool = inst(Opcodes.BASTORE);
  public static final JavaBytecode arrayLoadByteBool = inst(Opcodes.BALOAD);
  public static final JavaBytecode arrayStoreChar = inst(Opcodes.CASTORE);
  public static final JavaBytecode arrayLoadChar = inst(Opcodes.CALOAD);
  public static final JavaBytecode arrayStoreFloat = inst(Opcodes.FASTORE);
  public static final JavaBytecode arrayLoadFloat = inst(Opcodes.FALOAD);
  public static final JavaBytecode arrayStoreDouble = inst(Opcodes.DASTORE);
  public static final JavaBytecode arrayLoadDouble = inst(Opcodes.DALOAD);
  public static final JavaBytecode literalIntM1 = inst(ICONST_M1);
  public static final JavaBytecode literalInt0 = inst(ICONST_0);
  public static final JavaBytecode literalInt1 = inst(ICONST_1);
  public static final JavaBytecode literalInt2 = inst(ICONST_2);
  public static final JavaBytecode literalInt3 = inst(ICONST_3);
  public static final JavaBytecode literalInt4 = inst(ICONST_4);
  public static final JavaBytecode literalInt5 = inst(ICONST_5);
  public static final JavaBytecode literalLong0 = inst(LCONST_0);
  public static final JavaBytecode literalLong1 = inst(LCONST_1);
  public static final JavaBytecode returnVoid = inst(RETURN);
  public static final JavaBytecode returnIntShortByteBool = inst(IRETURN);
  public static final JavaBytecode returnLong = inst(LRETURN);
  public static final JavaBytecode returnFloat = inst(FRETURN);
  public static final JavaBytecode returnDouble = inst(DRETURN);
  public static final JavaBytecode returnObj = inst(ARETURN);
  public static JavaBytecode literalNull = inst(ACONST_NULL);

  public static boolean empty(JavaBytecode e) {
    if (e == null) return true;
    if (e instanceof JavaBytecodeSequence && ((JavaBytecodeSequence) e).size() == 0) return true;
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
    if (location >= 0) code.line(location);
    code.add(new JavaBytecodeInstruction(new TypeInsnNode(NEW, klass.value)));
    code.add(dup);
    code.add(arguments);
    code.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(INVOKESPECIAL, klass.value, "<init>", desc.value, false)));
    return code;
  }

  public static JavaBytecode accessField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new FieldInsnNode(GETFIELD, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode setField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new FieldInsnNode(PUTFIELD, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode setStaticField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new FieldInsnNode(PUTSTATIC, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode accessStaticField(
      int location, JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new FieldInsnNode(GETSTATIC, klass.value, field, fieldDesc.value)));
    return code;
  }

  public static JavaBytecode callConstructor(
      int location, JavaInternalName klass, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(INVOKESPECIAL, klass.value, "<init>", methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callInterfaceMthod(
      int location, JavaInternalName klass, String method, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(INVOKEINTERFACE, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callMethod(
      int location, JavaInternalName klass, String method, JavaMethodDescriptor methodDesc) {
    final JavaBytecodeSequence code = new JavaBytecodeSequence();
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(INVOKEVIRTUAL, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode callStaticMethodReflect(Class klass, String method) {
    for (Method method1 : klass.getMethods()) {
      if (!method.equals(method1.getName())) continue;
      JavaDataDescriptor ret = JavaDataDescriptor.fromClass(method1.getReturnType());
      JavaDataDescriptor[] args = new JavaDataDescriptor[method1.getParameterCount()];
      for (int i = 0; i < method1.getParameters().length; i++) {
        args[i] = JavaDataDescriptor.fromClass(method1.getParameters()[i].getType());
      }
      return new JavaBytecodeInstruction(
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
    if (location >= 0) code.line(location);
    code.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(INVOKESTATIC, klass.value, method, methodDesc.value, false)));
    return code;
  }

  public static JavaBytecode literalString(String value) {
    return new JavaBytecodeInstruction(new LdcInsnNode(value));
  }

  public static JavaBytecode inst(int opcode) {
    return new JavaBytecodeInstruction(new InsnNode(opcode));
  }

  public static JavaBytecode literalLong(long value) {
    if (value == 0) return literalLong0;
    if (value == 1) return literalLong1;
    return new JavaBytecodeInstruction(new LdcInsnNode(value));
  }

  public static JavaBytecode literalBool(boolean value) {
    return literalIntShortByte(value ? 1 : 0);
  }

  public static JavaBytecode literalIntShortByte(int value) {
    switch (value) {
      case -1:
        return literalIntM1;
      case 0:
        return literalInt0;
      case 1:
        return literalInt1;
      case 2:
        return literalInt2;
      case 3:
        return literalInt3;
      case 4:
        return literalInt4;
      case 5:
        return literalInt5;
    }
    return new JavaBytecodeInstruction(new LdcInsnNode(value));
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
    return new JavaBytecodeInstruction(new TypeInsnNode(CHECKCAST, toClass.value));
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
      if (at == prevAt) {
        out.value.removeLast();
        out.value.add(at.getCanonicalName());
        break;
      } else {
        out.value.add(at.getSimpleName());
      }
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
    return new JavaBytecodeStoreLoad(ILOAD, key);
  }

  public static JavaBytecode storeIntShortByteBool(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(ISTORE, key);
  }

  public static JavaBytecode loadLong(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(LLOAD, key);
  }

  public static JavaBytecode storeLong(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(LSTORE, key);
  }

  public static JavaBytecode loadFloat(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(FLOAD, key);
  }

  public static JavaBytecode storeFloat(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(FSTORE, key);
  }

  public static JavaBytecode loadDouble(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(DLOAD, key);
  }

  public static JavaBytecode storeDouble(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(DSTORE, key);
  }

  public static JavaBytecode loadObj(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(ALOAD, key);
  }

  public static JavaBytecode storeObj(JavaBytecodeBindingKey key) {
    return new JavaBytecodeStoreLoad(ASTORE, key);
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
