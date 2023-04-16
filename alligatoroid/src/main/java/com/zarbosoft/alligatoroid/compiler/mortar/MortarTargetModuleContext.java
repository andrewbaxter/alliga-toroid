package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstructionObj;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeJump;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeLand;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongTarget;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

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
        new JavaBytecodeInstructionObj(
            new TypeInsnNode(NEW, JavaBytecodeUtils.internalNameFromClass(TSList.class).value)));
    javaBytecodeSequence.add(JavaBytecodeUtils.dup);
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
        new JavaBytecodeInstructionObj(
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
  public TSSet<MortarDefinitionSet> dependencies = new TSSet<>();

  public MortarTargetModuleContext(String moduleInternalName) {
    this.moduleInternalName = moduleInternalName;
  }

  public static boolean convertFunctionArgumentRoot(
      EvaluationContext context,
      Location location,
      JavaBytecodeSequence code,
      Value argument) {
    boolean bad = false;
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        code.add((JavaBytecodeSequence) e.effect);
        if (!convertFunctionArgument(context, location,  code, e.value)) {
          bad = true;
          continue;
        }
      }
      return !bad;
    } else {
      return convertFunctionArgument(context, location,  code, argument);
    }
  }

  public static boolean convertFunctionArgument(
      EvaluationContext context,
      Location location,
      JavaBytecodeSequence code,
      Value argument) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value variable = ectx.record(argument.vary(context, location));
    if (variable == ErrorValue.value) {
      return false;
    }
    ectx.recordEffect(((MortarDataValue) variable).consume(context, location));
    code.add(((MortarTargetCode)ectx.build(null).effect).e);
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
  public TargetCode codeLand(JumpKey jumpKey) {
    return new MortarTargetCode(new JavaBytecodeLand(jumpKey));
  }

  @Override
  public TargetCode codeJump(JumpKey jumpKey) {
    return new MortarTargetCode(new JavaBytecodeJump(jumpKey));
  }

  @Override
  public EvaluateResult realizeRecord(EvaluationContext context, Location id, LooseRecord looseRecord) {
    TSOrderedMap<Object, MortarDataTypestate> types = new TSOrderedMap();
    final TSMap<Object, Object> data = new TSMap<>();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    for (ROPair<Object, EvaluateResult> e : looseRecord.data) {
      Value exported = ectx.record(ectx.record(e.second).export(context, id));
      if (!(exported instanceof MortarDataValue)) {
        throw new Assertion();
      }
      types.put(e.first, ((MortarDataValue) exported).type());
      data.put(e.first, ((MortarDataValue) exported).getInner());
    }
    return ectx.build(new MortarRecordTypestate(types).typestate_constAsValue(Record.create(data)));
  }

  @Override
  public EvaluateResult realizeTuple(EvaluationContext context, Location id, LooseTuple looseTuple) {
    TODO();
  }

  public JavaBytecodeSequence transfer(Object object) {
    final ObjId idObj = new ObjId(object);
    String name = transfers.getOpt(idObj);
    if (name == null) {
      name = TRANSFER_PREFIX + transfers.size();
      transfers.put(idObj, name);
    }
    JavaBytecodeSequence javaBytecodeSequence = new JavaBytecodeSequence();
    javaBytecodeSequence.add(
        new JavaBytecodeInstructionObj(
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
      if (chunk == null) {
        continue;
      }
      if (!(chunk instanceof JavaBytecode)) {
        throw new Assertion();
      }
      code.add((JavaBytecode) chunk);
    }
    return new MortarTargetCode(code);
  }

  public static class HalfLowerResult {
    public final MortarDataTypestate dataType;
    public final JavaBytecode valueCode;

    public HalfLowerResult(MortarDataTypestate dataType, JavaBytecode valueCode) {
      this.dataType = dataType;
      this.valueCode = valueCode;
    }
  }
}
