package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstruction;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongTarget;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
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
  public static JavaBytecodeSequence newTSListCode;
  public static JavaBytecodeSequence tsListAddCode;

  static {
    JavaBytecodeSequence javaBytecodeSequence = new JavaBytecodeSequence();
    javaBytecodeSequence.add(
        new JavaBytecodeInstruction(
            new TypeInsnNode(NEW, JavaBytecodeUtils.internalNameFromClass(TSList.class).value)));
    javaBytecodeSequence.add(new JavaBytecodeInstruction(new InsnNode(DUP)));
    newTSListCode =
        javaBytecodeSequence.add(
            JavaBytecodeUtils.callConstructor(
                -1,
                JavaBytecodeUtils.internalNameFromClass(TSList.class),
                JavaMethodDescriptor.fromConstructorParts(ROList.empty)));
  }

  static {
    JavaBytecodeSequence javaBytecodeSequence = new JavaBytecodeSequence();
    javaBytecodeSequence.add(
        new JavaBytecodeInstruction(
            new MethodInsnNode(
                INVOKEVIRTUAL,
                JavaBytecodeUtils.internalNameFromClass(TSList.class).value,
                "add",
                JavaMethodDescriptor.fromParts(
                        JavaDataDescriptor.fromObjectClass(TSList.class),
                        new TSList<>(JavaDataDescriptor.OBJECT))
                    .value,
                false)));
    tsListAddCode = javaBytecodeSequence;
  }

  public final TSOrderedMap<ObjId<Object>, String> transfers = new TSOrderedMap<>();
  public final String moduleInternalName;
  public TSSet<DefinitionSet> dependencies = new TSSet<>();

  public MortarTargetModuleContext(String moduleInternalName) {
    this.moduleInternalName = moduleInternalName;
  }

  public static boolean convertFunctionArgumentRoot(
      EvaluationContext context,
      Location location,
      JavaBytecodeSequence pre,
      JavaBytecodeSequence code,
      JavaBytecodeSequence post,
      Value argument) {
    boolean bad = false;
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        pre.add((JavaBytecodeSequence) e.preEffect);
        if (!convertFunctionArgument(context, location, pre, code, post, e.value)) {
          bad = true;
          continue;
        }
        post.add((JavaBytecode) e.postEffect);
      }
      return !bad;
    } else {
      return convertFunctionArgument(context, location, pre, code, post, argument);
    }
  }

  public static boolean convertFunctionArgument(
      EvaluationContext context,
      Location location,
      JavaBytecodeSequence pre,
      JavaBytecodeSequence code,
      JavaBytecodeSequence post,
      Value argument) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value variable = ectx.record(argument.vary(context, location));
    if (variable == ErrorValue.error) return false;
    code.add(((MortarTargetCode) ((DataValue) variable).consume(context, location)).e);
    final EvaluateResult prePost = ectx.build(null);
    pre.add((JavaBytecodeSequence) prePost.preEffect);
    post.add((JavaBytecode) prePost.postEffect);
    return true;
  }

  public static boolean assertTarget(EvaluationContext context, Location location) {
    if (context.target.getClass() != MortarTargetModuleContext.class) {
      context.errors.add(new WrongTarget(location, ID, context.target.id()));
      return false;
    }
    return true;
  }

  @Override
  public Id id() {
    return ID;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id, Value value) {
    if (value instanceof VariableDataValue) return EvaluateResult.pure(value);
    if (value instanceof ConstDataValue)
      return ((ConstDataValue) value)
          .mortarType()
          .valueVary(context, id, ((ConstDataValue) value).getInner());
    return Meta.autoMortarHalfDataTypes.get(Value.class).valueVary(context, id, value);
  }

  public JavaBytecodeSequence transfer(Object object) {
    String name = transfers.getOpt(object);
    if (name == null) {
      name = TRANSFER_PREFIX + transfers.size();
      transfers.put(object, name);
    }
    JavaBytecodeSequence javaBytecodeSequence = new JavaBytecodeSequence();
    javaBytecodeSequence.add(
        new JavaBytecodeInstruction(
            new FieldInsnNode(
                GETSTATIC,
                moduleInternalName,
                name,
                JavaDataDescriptor.fromObjectClass(object.getClass()).value)));
    return (JavaBytecodeSequence) javaBytecodeSequence;
  }

  @Override
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> chunks) {
    JavaBytecodeSequence code = new JavaBytecodeSequence();
    for (TargetCode chunk : chunks) {
      if (chunk == null) continue;
      if (!(chunk instanceof JavaBytecode)) {
        throw new Assertion();
      }
      code.add((JavaBytecode) chunk);
    }
    return new MortarTargetCode(code);
  }

  public static class HalfLowerResult {
    public final MortarDataType dataType;
    public final JavaBytecode valueCode;

    public HalfLowerResult(MortarDataType dataType, JavaBytecode valueCode) {
      this.dataType = dataType;
      this.valueCode = valueCode;
    }
  }
}
