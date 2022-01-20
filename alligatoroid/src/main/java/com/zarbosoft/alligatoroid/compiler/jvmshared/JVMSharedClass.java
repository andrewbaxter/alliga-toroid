package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;

public class JVMSharedClass {
  public final ClassWriter cw;
  public final TSSet<String> seenFunctions = new TSSet<>();

  public JVMSharedClass(JVMSharedJVMName jvmName) {
    this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(52, ACC_PUBLIC + ACC_SUPER, jvmName.value, null, "java/lang/Object", null);
  }

  public JVMSharedClass setMetaSource(String address) {
    cw.visitSource(address, null);
    return this;
  }

  public JVMSharedClass defineConstructor(
      JVMSharedFuncDescriptor desc, JVMSharedCode code, TSList<Object> initialIndexes) {
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

  public JVMSharedClass defineFunction(
      String methodId,
      JVMSharedFuncDescriptor desc,
      JVMSharedCode code,
      TSList<Object> initialIndexes) {
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

  public JVMSharedClass defineStaticField(String name, Class t) {
    cw.visitField(
        ACC_PUBLIC + ACC_STATIC, name, Type.getDescriptor(t), Type.getDescriptor(t), null);
    return this;
  }

  public boolean isDefined(String name) {
    return seenFunctions.contains(name);
  }
}
