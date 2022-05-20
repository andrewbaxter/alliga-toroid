package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;

public class JavaClass {
  public final ClassWriter cw;
  public final TSSet<String> seenFunctions = new TSSet<>();

  public JavaClass(JavaInternalName jvmName) {
    this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(52, ACC_PUBLIC + ACC_SUPER, jvmName.value, null, "java/lang/Object", null);
  }

  @Meta.WrapExpose
  public JavaClass setMetaSource(String address) {
    cw.visitSource(address, null);
    return this;
  }

  @Meta.WrapExpose
  public JavaClass defineConstructor(
      JavaMethodDescriptor desc,
      JavaBytecodeSequence code,
      TSList<JavaBytecodeBindingKey> initialIndexes) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", desc.value, null, null);
    mv.visitCode();
    MethodNode temp = new MethodNode();
    code.render(temp, initialIndexes);
    // JVMSharedCode.print(temp);
    code.render(mv, initialIndexes);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
    return this;
  }

  @Meta.WrapExpose
  public JavaClass defineFunction(
      String methodId,
      JavaMethodDescriptor desc,
      JavaBytecodeSequence code,
      TSList<JavaBytecodeBindingKey> initialIndexes) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, methodId, desc.value, null, null);
    mv.visitCode();
    MethodNode temp = new MethodNode();
    code.render(temp, initialIndexes);
    // JVMSharedCode.print(temp);
    code.render(mv, initialIndexes);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
    return this;
  }

  public byte[] render() {
    cw.visitEnd();

    /*
       uncheck( // DEBUG
           () -> {
             try (OutputStream os = Files.newOutputStream(Paths.get("dump.class"))) {
               os.write(cw.toByteArray());
             }
           });
       System.out.println("JVM CHECK");
       CheckClassAdapter.verify(new ClassReader(cw.toByteArray()), true, new PrintWriter(System.out));
       System.out.println("END JVM CHECK");
    */
    return cw.toByteArray();
  }

  @Meta.WrapExpose
  public JavaClass defineStaticField(String name, JavaDataDescriptor t) {
    cw.visitField(ACC_PUBLIC + ACC_STATIC, name, t.value, t.value, null);
    return this;
  }

  @Meta.WrapExpose
  public boolean isDefined(String name) {
    return seenFunctions.contains(name);
  }
}
