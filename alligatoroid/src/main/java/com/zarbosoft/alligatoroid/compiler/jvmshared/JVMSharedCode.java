package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.NEW;

public class JVMSharedCode implements TargetCode, JVMSharedCodeElement {
  public static final JVMSharedCodeElement boxBool =
      box(JVMSharedDataDescriptor.BOOL, JVMSharedJVMName.BOOL);
  public static final JVMSharedCodeElement boxByte =
      box(JVMSharedDataDescriptor.BYTE, JVMSharedJVMName.BYTE);
  public static final JVMSharedCodeElement boxInt =
      box(JVMSharedDataDescriptor.INT, JVMSharedJVMName.INT);
  private final TSList<JVMSharedCodeElement> children = new TSList<>();

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

  public static JVMSharedCodeElement instantiate(
      int location,
      JVMSharedJVMName klass,
      JVMSharedFuncDescriptor desc,
      JVMSharedCodeElement arguments) {
    JVMSharedCode code = new JVMSharedCode();
    if (location >= 0) code.line(location);
    code.add(new TypeInsnNode(NEW, klass.value)).addI(DUP);
    code.add(arguments);
    code.add(new MethodInsnNode(INVOKESPECIAL, klass.value, "<init>", desc.value, false));
    return code;
  }

  public static JVMSharedCodeElement accessField(
      int location, JVMSharedJVMName klass, String field, JVMSharedDataDescriptor fieldDesc) {
    final JVMSharedCode code = new JVMSharedCode();
    if (location >= 0) code.line(location);
    code.add(new FieldInsnNode(GETFIELD, klass.value, field, fieldDesc.value));
    return code;
  }

  public static JVMSharedCodeElement accessStaticField(
      int location, JVMSharedJVMName klass, String field, JVMSharedDataDescriptor fieldDesc) {
    final JVMSharedCode code = new JVMSharedCode();
    if (location >= 0) code.line(location);
    code.add(new FieldInsnNode(GETSTATIC, klass.value, field, fieldDesc.value));
    return code;
  }

  public static JVMSharedCodeElement callMethod(
      int location, JVMSharedJVMName klass, String method, JVMSharedFuncDescriptor methodDesc) {
    final JVMSharedCode code = new JVMSharedCode();
    if (location >= 0) code.line(location);
    code.add(new MethodInsnNode(INVOKEVIRTUAL, klass.value, method, methodDesc.value, false));
    return code;
  }

  public static JVMSharedCodeElement callStaticMethod(
      int location, JVMSharedJVMName klass, String method, JVMSharedFuncDescriptor methodDesc) {
    final JVMSharedCode code = new JVMSharedCode();
    if (location >= 0) code.line(location);
    code.add(new MethodInsnNode(INVOKESTATIC, klass.value, method, methodDesc.value, false));
    return code;
  }

  public static JVMSharedCodeElement string(String value) {
    return new JVMSharedCodeInstruction(new LdcInsnNode(value));
  }

  public static JVMSharedCodeElement inst(int opcode) {
    return new JVMSharedCodeInstruction(new InsnNode(opcode));
  }

  public static JVMSharedCodeElement int_(int value) {
    switch (value) {
      case -1:
        return inst(ICONST_M1);
      case 0:
        return inst(ICONST_0);
      case 1:
        return inst(ICONST_1);
      case 2:
        return inst(ICONST_2);
      case 3:
        return inst(ICONST_3);
      case 4:
        return inst(ICONST_4);
      case 5:
        return inst(ICONST_5);
    }
    return new JVMSharedCodeInstruction(new LdcInsnNode(value));
  }

  public static JVMSharedCodeElement box(
      JVMSharedDataDescriptor primDescriptor, JVMSharedJVMName box) {
    return callStaticMethod(
        -1,
        box,
        "valueOf",
        JVMSharedFuncDescriptor.fromParts(
            JVMSharedDataDescriptor.fromJVMName(box), primDescriptor));
  }

  public static JVMSharedCodeElement cast(JVMSharedDataDescriptor toClass) {
    return new JVMSharedCodeInstruction(new TypeInsnNode(CHECKCAST, toClass.value));
  }

  public JVMSharedCode addString(String value) {
    add(string(value));
    return this;
  }

  public JVMSharedCode addInt(int value) {
    add(int_(value));
    return this;
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handleNested(this);
  }

  public JVMSharedCode line(Integer line) {
    if (line != null) {
      LabelNode label = new LabelNode();
      add(label);
      add(new LineNumberNode(line, label));
    }
    return this;
  }

  public void render(MethodVisitor out, TSList<Object> initialIndexes) {
    TSList<JVMSharedCodeElement> children = new TSList<>();

    // Flatten for ease of use, find last uses
    TSMap<Object, Integer> lastUses = new TSMap<>();
    for (Object key : initialIndexes) {
      lastUses.put(key, -1);
    }

    ArrayDeque<Iterator<JVMSharedCodeElement>> stack = new ArrayDeque<>();
    {
      Iterator<JVMSharedCodeElement> iter = this.children.iterator();
      if (iter.hasNext()) stack.addLast(iter);
    }
    while (!stack.isEmpty()) {
      JVMSharedCodeElement next;
      {
        Iterator<JVMSharedCodeElement> iter = stack.peekLast();
        next = iter.next();
        if (!iter.hasNext()) stack.removeLast();
      }

      next.dispatch(
          new Dispatcher() {
            @Override
            public void handleNested(JVMSharedCode code) {
              Iterator<JVMSharedCodeElement> iter = code.children.iterator();
              if (iter.hasNext()) stack.addLast(iter);
            }

            @Override
            public void handleStoreLoad(JVMSharedCodeStoreLoad storeLoad) {
              lastUses.putReplace(storeLoad.key, children.size());
              children.add(storeLoad);
            }

            @Override
            public void handleInstruction(JVMSharedCodeInstruction instruction) {
              children.add(instruction);
            }
          });
    }

    // Render, considering
    TSList<Object> indexes = initialIndexes.mut();
    for (int i = 0; i < children.size(); i++) {
      int finalI = i;
      JVMSharedCodeElement child = children.get(i);
      child.dispatch(
          new Dispatcher() {
            @Override
            public void handleNested(JVMSharedCode code) {
              throw new Assertion();
            }

            @Override
            public void handleStoreLoad(JVMSharedCodeStoreLoad storeLoad) {
              Object childKey = storeLoad.key;
              int index = -1;
              if (storeLoad.code == ISTORE
                  || storeLoad.code == LSTORE
                  || storeLoad.code == ASTORE
                  || storeLoad.code == BASTORE
                  || storeLoad.code == FSTORE
                  || storeLoad.code == DSTORE) {
                // Handle store
                for (int j = 0; j < indexes.size(); j++) {
                  {
                    Object lastKey;
                    if ((lastKey = indexes.get(j)) != null && (lastUses.get(lastKey) > finalI)) {
                      // Can't use this slot, go to next
                      continue;
                    }
                  }
                  indexes.set(j, childKey);
                  index = j;
                }
                if (index == -1) {
                  index = indexes.size();
                  indexes.add(childKey);
                }
              } else {
                // Handle load - must already exist
                for (int j = 0; j < indexes.size(); j++) {
                  if (indexes.get(j) == childKey) {
                    index = j;
                    break;
                  }
                }
                if (index == -1) throw new Assertion();
              }
              out.visitVarInsn(storeLoad.code, index);
            }

            @Override
            public void handleInstruction(JVMSharedCodeInstruction instruction) {
              instruction.node.accept(out);
            }
          });
    }
  }

  public JVMSharedCode add(JVMSharedCodeElement element) {
    children.add(element);
    return this;
  }

  public JVMSharedCode add(AbstractInsnNode node) {
    children.add(new JVMSharedCodeInstruction(node));
    return this;
  }

  public JVMSharedCode addI(int opcode) {
    children.add(new JVMSharedCodeInstruction(new InsnNode(opcode)));
    return this;
  }

  public JVMSharedCode add(JVMSharedCode child) {
    if (child != null) children.add(child);
    return this;
  }

  public JVMSharedCodeElement bool_(boolean value) {
    return inst(value ? ICONST_1 : ICONST_0);
  }

  public JVMSharedCode addVarInsn(int opcode, Object key) {
    return add(new JVMSharedCodeStoreLoad(opcode, key));
  }

  public JVMSharedCode addBool(boolean value) {
    return add(bool_(value));
  }
}
