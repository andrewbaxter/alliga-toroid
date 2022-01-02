package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LSTORE;

public abstract class JVMSharedCode<M extends JVMSharedCode<M>>
    implements TargetCode, JVMSharedCodeElement {
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

  public M addString(String value) {
    add(string(value));
    return (M) this;
  }

  public M addInt(int value) {
    add(int_(value));
    return (M) this;
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handleNested(this);
  }

  public void line(Integer line) {
    if (line != null) {
      LabelNode label = new LabelNode();
      add(label);
      add(new LineNumberNode(line, label));
    }
  }

  public void render(MethodVisitor out, TSList<Object> initialIndexes) {
    TSList<JVMSharedCodeElement> children = new TSList<>();

    // Flatten for ease of use, find last uses
    TSMap<Object, Integer> lastUses = new TSMap<>();
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
            public void handleNested(JVMSharedCode<?> code) {
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
            public void handleNested(JVMSharedCode<?> code) {
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
                    Integer lastIndex;
                    if ((lastKey = indexes.get(j)) != null
                        && (lastIndex = lastUses.get(lastKey)) != null
                        && (lastIndex > finalI)) {
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

  public M add(JVMSharedCodeElement element) {
    children.add(element);
    return (M) this;
  }

  public M add(AbstractInsnNode node) {
    children.add(new JVMSharedCodeInstruction(node));
    return (M) this;
  }

  public M addI(int opcode) {
    children.add(new JVMSharedCodeInstruction(new InsnNode(opcode)));
    return (M) this;
  }

  public M add(JVMSharedCode<M> child) {
    if (child != null) children.add(child);
    return (M) this;
  }

  public JVMSharedCodeElement bool_(boolean value) {
    return inst(value ? ICONST_1 : ICONST_0);
  }

  public M addVarInsn(int opcode, Object key) {
    return add(new JVMSharedCodeStoreLoad(opcode, key));
  }

  public M addBool(boolean value) {
    return add(bool_(value));
  }
}
