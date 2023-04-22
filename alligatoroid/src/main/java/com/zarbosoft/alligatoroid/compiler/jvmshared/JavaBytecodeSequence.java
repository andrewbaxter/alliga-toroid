package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ASM8;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFGE;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.IFLE;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ACMPNE;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.JSR;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JavaBytecodeSequence implements JavaBytecode, AutoExportable {
  interface AsmFramingVisitor {
    void handleTable(Label dflt, Label[] labels);

    void handleBranch(Label label);

    void handleJump(Label label);

    void handleLabel(Label label);
  }

  static void dispatchAsmFraming(AbstractInsnNode n, AsmFramingVisitor v) {
    n.accept(
        new MethodVisitor(ASM8) {
          @Override
          public void visitInsn(int opcode) {
            throw new Assertion(); // Should be using the Int obj
          }

          @Override
          public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            throw new Assertion(); // No such node, so this can't exist AFAIK
          }

          @Override
          public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            v.handleTable(dflt, labels);
          }

          @Override
          public void visitJumpInsn(int opcode, Label label) {
            switch (opcode) {
              case IFEQ:
              case IFNE:
              case IFLT:
              case IFGE:
              case IFGT:
              case IFLE:
              case IF_ICMPEQ:
              case IF_ICMPNE:
              case IF_ICMPLT:
              case IF_ICMPGE:
              case IF_ICMPGT:
              case IF_ICMPLE:
              case IF_ACMPEQ:
              case IF_ACMPNE:
              case IFNULL:
              case IFNONNULL:
                v.handleBranch(label);
                break;
              case GOTO:
                v.handleJump(label);
                break;
              case JSR:
                // What is this
                throw new Assertion();
              default:
                throw new Assertion();
            }
          }

          @Override
          public void visitLabel(Label label) {
            v.handleLabel(label);
          }
        });
  }

  public TSList<JavaBytecode> children = new TSList<>();

  private void walk(Dispatcher<Iterator<JavaBytecode>> dispatcher) {
    ArrayDeque<Iterator<JavaBytecode>> stack = new ArrayDeque<>();
    {
      Iterator<JavaBytecode> iter = this.children.iterator();
      if (iter.hasNext()) {
        stack.addLast(iter);
      }
    }
    while (!stack.isEmpty()) {
      JavaBytecode next;
      {
        Iterator<JavaBytecode> iter = stack.peekLast();
        next = iter.next();
        if (!iter.hasNext()) {
          stack.removeLast();
        }
      }
      final Iterator<JavaBytecode> res = next.dispatch(dispatcher);
      if (res != null && res.hasNext()) {
        stack.addLast(res);
      }
    }
  }

  public void render(MethodVisitor out, TSList<JavaBytecodeBindingKey> initialIndexes) {

    /// Pass 1, forward: Identify last catch blocks
    TSMap<JavaBytecodeCatchKey, Integer> catchLastUses = new TSMap<>();
    {
      final int[] catchIndex = {0};
      walk(
          new DefaultDispatcher<Iterator<JavaBytecode>>() {
            @Override
            public Iterator<JavaBytecode> handleSequence(JavaBytecodeSequence n) {
              return n.children.iterator();
            }

            @Override
            public Iterator<JavaBytecode> handleCatch(JavaBytecodeCatch n) {
              catchIndex[0] += 1;
              catchLastUses.putReplace(n.key, catchIndex[0]);
              return TSList.of(n.inner).iterator();
            }
          });
    }

    // Pass 2, forward: flatten, replacing catch blocks with instructions + labels; jumps
    TSList<JavaBytecode> flattened = new TSList<>();
    {
      TSMap<JumpKey, LabelNode> jumpLabels = new TSMap<>();
      TSMap<JavaBytecodeCatchKey, Label> catchStartLabels = new TSMap<>();
      TSMap<JavaBytecodeCatchKey, Label> catchFinallyLabels = new TSMap<>();
      final int[] catchIndex = {0};
      walk(
          new Dispatcher<Iterator<JavaBytecode>>() {
            @Override
            public Iterator<JavaBytecode> handleInstruction(JavaBytecodeInstruction n) {
              flattened.add(n);
              return null;
            }

            @Override
            public Iterator<JavaBytecode> handleLineNumber(JavaBytecodeLineNumber n) {
              flattened.add(n);
              return null;
            }

            @Override
            public Iterator<JavaBytecode> handleSequence(JavaBytecodeSequence n) {
              return n.children.iterator();
            }

            @Override
            public Iterator<JavaBytecode> handleStoreLoad(JavaBytecodeStoreLoad n) {
              flattened.add(n);
              return null;
            }

            @Override
            public Iterator<JavaBytecode> handleCatch(JavaBytecodeCatch n) {
              catchIndex[0] += 1;

              // End catchable code
              final Label endLabel = new Label();
              flattened.add(new JavaBytecodeInstructionObj(new LabelNode(endLabel)));
              flattened.add(
                  new JavaBytecodeInstructionTryCatch(
                      catchStartLabels.get(n.key),
                      endLabel,
                      catchFinallyLabels.get(n.key),
                      Global.INTNAME_RUNTIME_EXCEPTION.value));

              if (catchIndex[0] < catchLastUses.get(n.key)) {
                // Non-final (followed by early scope exit via jump)
                return TSList.of(
                        // Normal operation of drop
                        n.inner,
                        // Start next catchable segment
                        new JavaBytecodeOther<>(
                            () -> {
                              final Label newStartLabel = new Label();
                              flattened.add(
                                  new JavaBytecodeInstructionObj(new LabelNode(newStartLabel)));
                              catchStartLabels.put(n.key, newStartLabel);
                              return null;
                            }))
                    .iterator();
              } else {
                final LabelNode postLabelNode = new LabelNode(new Label());
                final JavaBytecodeBindingKey exceptionKey = new JavaBytecodeBindingKey();
                return TSList.of(
                        // Normal operation of drop
                        n.inner,
                        new JavaBytecodeOther<>(
                            () -> {
                              // If drop succeeds, move to after finally
                              flattened.add(
                                  new JavaBytecodeInstructionObj(
                                      new JumpInsnNode(GOTO, postLabelNode)));
                              // Start finally handler errors happening in nested code
                              flattened.add(
                                  new JavaBytecodeInstructionObj(
                                      new LabelNode(catchFinallyLabels.get(n.key))));
                              flattened.add(JavaBytecodeUtils.storeObj(exceptionKey));
                              return null;
                            }),
                        n.inner,
                        new JavaBytecodeOther<>(
                            () -> {
                              flattened.add(JavaBytecodeUtils.loadObj(exceptionKey));
                              flattened.add(JavaBytecodeUtils.inst(ATHROW));
                              // After the finally handler, proceed with originally scheduled code
                              flattened.add(new JavaBytecodeInstructionObj(postLabelNode));
                              return null;
                            }))
                    .iterator();
              }
            }

            @Override
            public Iterator<JavaBytecode> handleCatchStart(JavaBytecodeCatchStart n) {
              if (!catchLastUses.has(n.key)) {
                return null;
              }
              final Label startLabel = new Label();
              final Label handleLabel = new Label();
              flattened.add(new JavaBytecodeInstructionObj(new LabelNode(startLabel)));
              catchStartLabels.put(n.key, startLabel);
              catchFinallyLabels.putReplace(n.key, handleLabel);
              return null;
            }

            @Override
            public Iterator<JavaBytecode> handleLand(JavaBytecodeLand n) {
              flattened.add(
                  new JavaBytecodeInstructionObj(
                      jumpLabels.getCreate(n.jumpKey, () -> new LabelNode())));
              return null;
            }

            @Override
            public Iterator<JavaBytecode> handleJump(JavaBytecodeJump n) {
              flattened.add(
                  new JavaBytecodeInstructionObj(
                      new JumpInsnNode(
                          GOTO, jumpLabels.getCreate(n.jumpKey, () -> new LabelNode()))));
              return null;
            }
          });
    }

    // Pass 3, backward: following branches, identify last uses of each binding key
    TSMap<JavaBytecodeBindingKey, TSList<Integer>> bindingLastUses = new TSMap<>();
    {
      TSMap<JavaBytecodeBindingKey, Integer> totalUses = new TSMap<>();
      final PMap<JavaBytecodeBindingKey, Integer>[] currentBindingLastUses =
          new PMap[] {HashTreePMap.<JavaBytecodeBindingKey, Integer>empty()};
      TSMap<Label, PMap<JavaBytecodeBindingKey, Integer>> branchBindingLastUses = new TSMap<>();
      int index = 0;
      for (JavaBytecode e : new ReverseIterable<>(flattened)) {
        int finalIndex = index;
        e.dispatch(
            new DefaultDispatcher<Object>() {
              @Override
              public Object handleInstruction(JavaBytecodeInstruction n) {
                n.dispatchMore(
                    new JavaBytecodeInstruction.MoreDispatcher() {
                      @Override
                      public void handleObj(JavaBytecodeInstructionObj n) {
                        dispatchAsmFraming(
                            n.node,
                            new AsmFramingVisitor() {
                              @Override
                              public void handleTable(Label dflt, Label[] labels) {
                                final HashMap<JavaBytecodeBindingKey, Integer> merged =
                                    new HashMap<>(
                                        branchBindingLastUses.getOr(
                                            dflt, () -> HashTreePMap.empty()));
                                for (Label label : labels) {
                                  merged.putAll(
                                      branchBindingLastUses.getOr(
                                          label, () -> HashTreePMap.empty()));
                                }
                                currentBindingLastUses[0] = HashTreePMap.from(merged);
                              }

                              @Override
                              public void handleBranch(Label label) {
                                final HashMap<JavaBytecodeBindingKey, Integer> merged =
                                    new HashMap<>();
                                merged.putAll(currentBindingLastUses[0]);
                                merged.putAll(
                                    branchBindingLastUses.getOr(label, () -> HashTreePMap.empty()));
                                currentBindingLastUses[0] = HashTreePMap.from(merged);
                              }

                              @Override
                              public void handleJump(Label label) {
                                currentBindingLastUses[0] =
                                    branchBindingLastUses.getOr(label, () -> HashTreePMap.empty());
                              }

                              @Override
                              public void handleLabel(Label label) {
                                branchBindingLastUses.put(label, currentBindingLastUses[0]);
                              }
                            });
                      }

                      @Override
                      public void handleInt(JavaBytecodeInstructionInt n) {
                        if (n.code == ATHROW) {
                          // Catch instructions must come last
                          // Push exception ranges
                          // Activate range when end found (add to stack)
                          // Deactivate when start found (pop from stack)
                          // Match with active range based on type info
                          // ... Assume uncaught for now
                          currentBindingLastUses[0] = HashTreePMap.empty();
                        }
                      }

                      @Override
                      public void handleTryCatch(JavaBytecodeInstructionTryCatch n) {}
                    });
                return null;
              }

              @Override
              public Object handleStoreLoad(JavaBytecodeStoreLoad n) {
                if (currentBindingLastUses[0].get(n.key) == null) {
                  bindingLastUses.getCreate(n.key, () -> new TSList<>()).add(finalIndex);
                  currentBindingLastUses[0] = currentBindingLastUses[0].plus(n.key, finalIndex);
                }
                totalUses.update(n.key, () -> 0, v -> v + 1);
                return null;
              }
            });
        index += 1;
      }
      for (Map.Entry<JavaBytecodeBindingKey, TSList<Integer>> use : bindingLastUses) {
        final TSList<Integer> lastUses = use.getValue();
        for (int i = 0; i < lastUses.size(); i++) {
          lastUses.set(i, totalUses.get(use.getKey()) - 1 - lastUses.get(i));
        }
      }
    }

    // Pass 4, forward: generate bytecode
    {
      final PMap<JavaBytecodeBindingKey, Integer>[] currentIndexes =
          new PMap[] {HashTreePMap.empty()};
      final PVector<JavaBytecodeBindingKey>[] currentRevIndexes =
          new PVector[] {TreePVector.empty()};
      TSMap<Label, ROPair<PMap<JavaBytecodeBindingKey, Integer>, PVector<JavaBytecodeBindingKey>>>
          branchIndexes = new TSMap<>();
      for (int i = 0; i < initialIndexes.size(); i++) {
        final JavaBytecodeBindingKey key = initialIndexes.get(i);
        currentIndexes[0] = currentIndexes[0].plus(key, i);
        currentRevIndexes[0] = currentRevIndexes[0].plus(key);
      }
      int codeIndex = 0;
      for (JavaBytecode n : flattened) {
        int finalCodeIndex = codeIndex;
        n.dispatch(
            new Dispatcher<Object>() {
              @Override
              public Object handleInstruction(JavaBytecodeInstruction n) {
                n.dispatchMore(
                    new JavaBytecodeInstruction.MoreDispatcher() {
                      @Override
                      public void handleObj(JavaBytecodeInstructionObj n) {
                        n.node.accept(out);

                        dispatchAsmFraming(
                            n.node,
                            new AsmFramingVisitor() {
                              /**
                               * Copy current variable state into new frame, and if that frame was
                               * already the destination of some other jump/branch make sure the
                               * indexes match.
                               */
                              void setFrameInitialVars(Label l) {
                                final ROPair<
                                        PMap<JavaBytecodeBindingKey, Integer>,
                                        PVector<JavaBytecodeBindingKey>>
                                    old =
                                        branchIndexes.putReplace(
                                            l,
                                            new ROPair<>(currentIndexes[0], currentRevIndexes[0]));
                                if (old != null) {
                                  if (old.second.size() != currentRevIndexes[0].size()) {
                                    throw new Assertion();
                                  }
                                  for (int i = 0; i < old.second.size(); ++i) {
                                    if (currentRevIndexes[0].get(i) != old.second.get(i)) {
                                      throw new Assertion();
                                    }
                                  }
                                }
                              }

                              @Override
                              public void handleTable(Label dflt, Label[] labels) {
                                setFrameInitialVars(dflt);
                                for (Label label : labels) {
                                  setFrameInitialVars(label);
                                }
                                currentIndexes[0] = null;
                                currentRevIndexes[0] = null;
                              }

                              @Override
                              public void handleBranch(Label label) {
                                setFrameInitialVars(label);
                              }

                              @Override
                              public void handleJump(Label label) {
                                setFrameInitialVars(label);
                                currentIndexes[0] = null;
                                currentRevIndexes[0] = null;
                              }

                              @Override
                              public void handleLabel(Label label) {
                                ROPair<
                                        PMap<JavaBytecodeBindingKey, Integer>,
                                        PVector<JavaBytecodeBindingKey>>
                                    p = branchIndexes.removeGetOpt(label);
                                if (p != null) {
                                  currentIndexes[0] = p.first;
                                  currentRevIndexes[0] = p.second;
                                }
                              }
                            });
                      }

                      @Override
                      public void handleInt(JavaBytecodeInstructionInt n) {
                        out.visitInsn(n.code);
                      }

                      @Override
                      public void handleTryCatch(JavaBytecodeInstructionTryCatch n) {
                        out.visitTryCatchBlock(n.start, n.end, n.handler, n.excType);
                      }
                    });
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
                int index = currentIndexes[0].getOrDefault(storeLoad.key, -1);
                if (index == -1) {
                  if (storeLoad.code == ISTORE
                      || storeLoad.code == LSTORE
                      || storeLoad.code == ASTORE
                      || storeLoad.code == BASTORE
                      || storeLoad.code == FSTORE
                      || storeLoad.code == DSTORE) {
                    // Handle store
                    for (int j = 0; j < currentRevIndexes[0].size(); j++) {
                      if (currentRevIndexes[0].get(j) != null) {
                        continue;
                      }
                      currentIndexes[0] = currentIndexes[0].plus(storeLoad.key, j);
                      currentRevIndexes[0] = currentRevIndexes[0].with(j, storeLoad.key);
                      index = j;
                    }
                    if (index == -1) {
                      index = currentIndexes[0].size();
                      currentIndexes[0] = currentIndexes[0].plus(storeLoad.key, index);
                      currentRevIndexes[0] = currentRevIndexes[0].plus(storeLoad.key);
                    }
                  } else {
                    // Handle load - must already exist
                    throw new Assertion();
                  }
                }
                out.visitVarInsn(storeLoad.code, index);
                if (bindingLastUses.get(storeLoad.key).lastIndexOf(finalCodeIndex) != -1) {
                  currentIndexes[0] = currentIndexes[0].minus(storeLoad.key);
                  currentRevIndexes[0] = currentRevIndexes[0].with(index, null);
                }
                return null;
              }

              @Override
              public Object handleCatch(JavaBytecodeCatch n) {
                throw new Assertion();
              }

              @Override
              public Object handleCatchStart(JavaBytecodeCatchStart n) {
                throw new Assertion();
              }

              @Override
              public Object handleLand(JavaBytecodeLand n) {
                throw new Assertion();
              }

              @Override
              public Object handleJump(JavaBytecodeJump n) {
                throw new Assertion();
              }
            });
        codeIndex += 1;
      }
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
