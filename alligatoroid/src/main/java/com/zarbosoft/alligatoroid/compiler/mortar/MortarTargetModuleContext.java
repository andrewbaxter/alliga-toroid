package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongTarget;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;

public class MortarTargetModuleContext implements TargetModuleContext {
  public static final String TRANSFER_PREFIX = "transfer";
  public static final Id ID =
      new Id() {
        @Override
        public String toString() {
          return "mortar";
        }
      };
  public static JVMSharedCode newTSListCode =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMSharedJVMName.fromClass(TSList.class).value))
          .addI(DUP)
          .add(
              JVMSharedCode.callConstructor(
                  -1,
                  JVMSharedJVMName.fromClass(TSList.class),
                  JVMSharedFuncDescriptor.fromConstructorParts()));
  public static JVMSharedCode tsListAddCode =
      new JVMSharedCode()
          .add(
              new MethodInsnNode(
                  INVOKEVIRTUAL,
                  JVMSharedJVMName.fromClass(TSList.class).value,
                  "add",
                  JVMSharedFuncDescriptor.fromParts(
                          JVMSharedDataDescriptor.fromObjectClass(TSList.class),
                          JVMSharedDataDescriptor.OBJECT)
                      .value,
                  false));
  public final TSOrderedMap<Object, String> transfers = new TSOrderedMap<>();
  public final String moduleInternalName;

  public MortarTargetModuleContext(String moduleInternalName) {
    this.moduleInternalName = moduleInternalName;
  }

  public static boolean convertFunctionArgumentRoot(
      EvaluationContext context,
      Location location,
      JVMSharedCode pre,
      JVMSharedCode code,
      JVMSharedCode post,
      Value argument) {
    boolean bad = false;
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        pre.add((JVMSharedCode) e.preEffect);
        if (!convertFunctionArgument(context, location, pre, code, post, e.value)) {
          bad = true;
          continue;
        }
        post.add((JVMSharedCodeElement) e.postEffect);
      }
      return !bad;
    } else {
      return convertFunctionArgument(context, location, pre, code, post, argument);
    }
  }

  public static boolean convertFunctionArgument(
      EvaluationContext context,
      Location location,
      JVMSharedCode pre,
      JVMSharedCode code,
      JVMSharedCode post,
      Value argument) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value variable = ectx.record(argument.vary(context, location));
    if (variable == ErrorValue.error) return false;
    code.add(((DataValue) variable).mortarVaryCode(context, location).half(context));
    final EvaluateResult prePost = ectx.build(null);
    pre.add((JVMSharedCode) prePost.preEffect);
    post.add((JVMSharedCodeElement) prePost.postEffect);
    return true;
  }

  public static boolean assertTarget(EvaluationContext context, Location location) {
    if (context.target.getClass() != MortarTargetModuleContext.class) {
      context.moduleContext.errors.add(new WrongTarget(location, ID, context.target.id()));
      return false;
    }
    return true;
  }

  @Override
  public Id id() {
    return ID;
  }

  public JVMSharedCode transfer(Object object) {
    String name = transfers.getOpt(object);
    if (name == null) {
      name = TRANSFER_PREFIX + transfers.size();
      transfers.put(object, name);
    }
    return (JVMSharedCode)
        new JVMSharedCode()
            .add(
                new FieldInsnNode(
                    GETSTATIC,
                    moduleInternalName,
                    name,
                    JVMSharedDataDescriptor.fromObjectClass(object.getClass()).value));
  }

  @Override
  public JVMSharedCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> chunks) {
    JVMSharedCode code = new JVMSharedCode();
    for (TargetCode chunk : chunks) {
      if (chunk == null) continue;
      if (!(chunk instanceof JVMSharedCodeElement)) {
        throw new Assertion();
      }
      code.add((JVMSharedCodeElement) chunk);
    }
    return code;
  }

  @Override
  public boolean codeEmpty(TargetCode code) {
    return JVMSharedCodeElement.empty((JVMSharedCodeElement) code);
  }

  public static class HalfLowerResult {
    public final MortarDataType dataType;
    public final JVMSharedCodeElement valueCode;

    public HalfLowerResult(MortarDataType dataType, JVMSharedCodeElement valueCode) {
      this.dataType = dataType;
      this.valueCode = valueCode;
    }
  }
}
