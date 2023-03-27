package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayDeque;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JavaBytecodeSequence implements JavaBytecode, BuiltinAutoExportable {
  public TSList<JavaBytecode> children = new TSList<>();

  public void render(MethodVisitor out, TSList<JavaBytecodeBindingKey> initialIndexes) {
    TSList<JavaBytecode> children = new TSList<>();

    // Flatten for ease of use, find last uses
    TSMap<JavaBytecodeBindingKey, Integer> lastUses = new TSMap<>();
    for (JavaBytecodeBindingKey key : initialIndexes) {
      lastUses.put(key, -1);
    }

    ArrayDeque<Iterator<JavaBytecode>> stack = new ArrayDeque<>();
    {
      Iterator<JavaBytecode> iter = this.children.iterator();
      if (iter.hasNext()) stack.addLast(iter);
    }
    while (!stack.isEmpty()) {
      JavaBytecode next;
      {
        Iterator<JavaBytecode> iter = stack.peekLast();
        next = iter.next();
        if (!iter.hasNext()) stack.removeLast();
      }

      next.dispatch(
          new Dispatcher<Object>() {
            @Override
            public Object handleInstruction(JavaBytecodeInstructionInt n) {
              children.add(n);
              return null;
            }

            @Override
            public Object handleLineNumber(JavaBytecodeLineNumber n) {
              children.add(n);
              return null;
            }

            @Override
            public Object handleSequence(JavaBytecodeSequence code) {
              Iterator<JavaBytecode> iter = code.children.iterator();
              if (iter.hasNext()) stack.addLast(iter);
              return null;
            }

            @Override
            public Object handleStoreLoad(JavaBytecodeStoreLoad n) {
              JavaBytecodeStoreLoad storeLoad = (JavaBytecodeStoreLoad) next;
              lastUses.putReplace(storeLoad.key, children.size());
              children.add(storeLoad);
              return null;
            }
          });
    }

    // Render, considering
    TSList<JavaBytecodeBindingKey> indexes = initialIndexes.mut();
    for (int i = 0; i < children.size(); i++) {
      int finalI = i;
      JavaBytecode child = children.get(i);
      child.dispatch(
          new Dispatcher<Object>() {
            @Override
            public Object handleInstruction(JavaBytecodeInstructionInt n) {
            n.write(out);
              return null;
            }

            @Override
            public Object handleLineNumber(JavaBytecodeLineNumber n) {
              Label label = new Label();
              out.visitLabel(label);
              out.visitLineNumber(n.lineNumber, label);
              return null;
            }

            @Override
            public Object handleSequence(JavaBytecodeSequence n) {
              throw new Assertion();
            }

            @Override
            public Object handleStoreLoad(JavaBytecodeStoreLoad storeLoad) {
              JavaBytecodeBindingKey childKey = storeLoad.key;
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
                    JavaBytecodeBindingKey lastKey;
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
              return null;
            }
          });
    }
  }

  @StaticAutogen.WrapExpose
  public JavaBytecodeSequence add(JavaBytecode element) {
    if (element != null) {
      element.dispatch(
          new DefaultDispatcher<Object>() {
            @Override
            public Object handleDefault(JavaBytecode n) {
              children.add(n);
              return null;
            }

            @Override
            public Object handleSequence(JavaBytecodeSequence element) {
              children.addAll(element.children);
              return null;
            }
          });
    }
    return this;
  }

  @StaticAutogen.WrapExpose
  public int size() {
    return children.size();
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleSequence(this);
  }
}
