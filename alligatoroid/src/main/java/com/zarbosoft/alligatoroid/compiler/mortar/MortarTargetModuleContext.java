package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Global;
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
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ChainComparator;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ArrayList;
import java.util.List;

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
    javaBytecodeSequence.add(Global.JBC_DUP);
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
                        new TSList<>(Global.DESC_OBJECT))
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
      EvaluationContext context, Location location, JavaBytecodeSequence code, Value argument) {
    boolean bad = false;
    if (argument instanceof LooseRecord) {
      TSList<Value> convertedValues = new TSList<>();
      for (ROPair<Object, EvaluateResult> e : ((LooseRecord) argument).data) {
        code.add(MortarTargetCode.ex(e.second.effect));
        final Value convertedValue =
            convertFunctionArgument(context, location, code, e.second.value);
        if (convertedValue == ErrorValue.value) {
          bad = true;
          continue;
        }
        convertedValues.add(convertedValue);
      }
      for (Value value : new ReverseIterable<>(convertedValues)) {
        code.add(MortarTargetCode.ex(value.cleanup(context, location)));
      }
      return !bad;
    } else {
      final Value convertedValue = convertFunctionArgument(context, location, code, argument);
      code.add((MortarTargetCode.ex(convertedValue.cleanup(context, location))));
      return convertedValue != ErrorValue.value;
    }
  }

  public static Value convertFunctionArgument(
      EvaluationContext context, Location location, JavaBytecodeSequence code, Value argument) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value value = ectx.record(argument.vary(context, location));
    if (value == ErrorValue.value) {
      return ErrorValue.value;
    }
    ectx.recordEffect(((MortarDataValue) value).consume(context, location));
    code.add(MortarTargetCode.ex(ectx.build(null).effect));
    return value;
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

  public static enum SuperComparableType {
    Int,
    Bool,
    String,
    Tuple
  }

  public static class SuperComparable implements Comparable<SuperComparable> {
    public final SuperComparableType type;
    public final Object data;

    private SuperComparable(SuperComparableType type, Object data) {
      this.type = type;
      this.data = data;
    }

    public static SuperComparable int_(int value) {
      return new SuperComparable(SuperComparableType.Int, value);
    }

    public static SuperComparable bool(boolean value) {
      return new SuperComparable(SuperComparableType.Bool, value ? 1 : 0);
    }

    public static SuperComparable string(String value) {
      return new SuperComparable(SuperComparableType.String, value);
    }

    public static SuperComparable tuple(SuperComparable... value) {
      return new SuperComparable(SuperComparableType.Tuple, value.clone());
    }

    @Override
    public int compareTo(@NotNull SuperComparable o) {
      int typeComp = type.compareTo(o.type);
      if (typeComp != 0) {
        return typeComp;
      }
      switch (type) {
        case Int:
        case Bool:
          return (int) data - (int) o.data;
        case String:
          return ((String) data).compareTo((String) o.data);
        case Tuple:
          {
            SuperComparable[] data = (SuperComparable[]) this.data;
            SuperComparable[] otherData = (SuperComparable[]) o.data;
            final int tieBreaker = data.length - otherData.length;
            final int useLength = tieBreaker < 0 ? data.length : otherData.length;
            for (int i = 0; i < useLength; i += 1) {
              int res = data[i].compareTo(otherData[i]);
              if (res != 0) {
                return res;
              }
            }
            return tieBreaker;
          }
        default:
          throw new DeadCode();
      }
    }
  }

  @Override
  public EvaluateResult realizeRecord(
      EvaluationContext context, Location id, LooseRecord looseRecord) {
    boolean allConst = true;
    List<Object> keys = new ArrayList<>();
    for (ROPair<Object, EvaluateResult> e : looseRecord.data) {
      allConst = allConst && e.second.value instanceof MortarDataValueConst;
      keys.add(e.first);
    }

    keys.sort(
        new ChainComparator<Object>()
            .lesserFirst(
                e -> {
                  if (e instanceof Boolean) {
                    return SuperComparable.bool((Boolean) e);
                  } else if (e instanceof Integer) {
                    return SuperComparable.int_((Integer) e);
                  } else if (e instanceof String) {
                    return SuperComparable.string((String) e);
                  } else {
                    throw new Assertion();
                  }
                })
            .build());
    TSMap<Object, Integer> keyOrder = new TSMap<>();
    for (int i = 0; i < keys.size(); i++) {
      keyOrder.put(keys.get(i), i);
    }

    if (allConst) {
      ROPair<Object, MortarRecordFieldstate>[] types = new ROPair[looseRecord.data.size()];
      Object[] record = new Object[looseRecord.data.size()];
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
      for (int i = 0; i < looseRecord.data.size(); i += 1) {
        final ROPair<Object, EvaluateResult> e = looseRecord.data.getI(i);
        final MortarDataValueConst value = (MortarDataValueConst) ectx.record(e.second);
        final MortarDataType valueType = value.type(context);
        record[keyOrder.get(i)] = value.constConsume(context, id);
        types[keyOrder.get(i)] =
            new ROPair<>(
                e.first,
                ((MortarRecordFieldable) valueType).newTupleField(i).recordfield_newFieldstate());
      }
      return ectx.build(
          new MortarDataValueConst(new MortarRecordTypestate(TSList.of(types)), record));
    } else {
      ROPair<Object, EvaluateResult>[] working = new ROPair[looseRecord.data.size()];
      for (int i = 0; i < looseRecord.data.size(); i += 1) {
        working[keyOrder.get(i)] = new ROPair<>(i, looseRecord.data.get(i));
      }
      return MortarRecordTypestate.newTupleCode(context, id, TSList.of(working));
    }
  }

  @Override
  public boolean looseRecordCanCastTo(
      EvaluationContext context, LooseRecord looseRecord, AlligatorusType type) {
    if (!(type instanceof MortarRecordType)) {
      return false;
    }
    final MortarRecordTypestate other = (MortarRecordTypestate) type;
    if (other.fields.size() != looseRecord.data.size()) {
      return false;
    }
    for (int i = 0; i < looseRecord.data.size(); i++) {
      final ROPair<Object, EvaluateResult> looseEl = looseRecord.data.getI(i);
      if (!looseEl.second.value.canCastTo(
          context, other.fields.get(i).second.recordfieldstate_asType())) {
        return false;
      }
      if (!other.fieldLookup.getOpt(looseEl.first).equals(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public EvaluateResult looseRecordCastTo(
      EvaluationContext context,
      Location location,
      ROOrderedMap<Object, EvaluateResult> data,
      AlligatorusType type) {
    boolean allConst = true;
    for (ROPair<Object, EvaluateResult> e : data) {
      allConst = allConst && e.second.value instanceof MortarDataValueConst;
    }

    TSMap<Object, Integer> keyOrder = new TSMap<>();
    final ROList<ROPair<Object, MortarRecordField>> destFields = ((MortarRecordType) type).fields;
    for (int i = 0; i < destFields.size(); i++) {
      keyOrder.put(destFields.get(i).first, i);
    }

    if (allConst) {
      ROPair<Object, MortarRecordFieldstate>[] types = new ROPair[data.size()];
      Object[] record = new Object[data.size()];
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      for (int i = 0; i < data.size(); i += 1) {
        final ROPair<Object, EvaluateResult> e = data.getI(i);
        final int destIndex = keyOrder.get(i);
        final MortarDataValueConst value =
            (MortarDataValueConst)
                ectx.record(
                    ectx.record(e.second)
                        .castTo(
                            context,
                            location,
                            destFields.get(destIndex).second.recordfield_asType()));
        final MortarDataType valueType = value.type(context);
        record[destIndex] = value.constConsume(context, location);
        types[destIndex] =
            new ROPair<>(
                e.first,
                ((MortarRecordFieldable) valueType).newTupleField(i).recordfield_newFieldstate());
      }
      return ectx.build(
          new MortarDataValueConst(new MortarRecordTypestate(TSList.of(types)), record));
    } else {
      ROPair<Object, EvaluateResult>[] working = new ROPair[data.size()];
      for (int i = 0; i < data.size(); i += 1) {
        final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
        final int destIndex = keyOrder.get(i);
        working[destIndex] =
            new ROPair<>(
                i,
                ectx.build(
                    ectx.record(
                        ectx.record(data.get(i))
                            .castTo(
                                context,
                                location,
                                destFields.get(destIndex).second.recordfield_asType()))));
      }
      return MortarRecordTypestate.newTupleCode(context, location, TSList.of(working));
    }
  }

  @Override
  public AlligatorusType looseRecordType(EvaluationContext context, LooseRecord looseRecord) {
    TSList<ROPair<Object, MortarRecordField>> fields = new TSList<>();
    for (int i = 0; i < looseRecord.data.size(); i++) {
      final ROPair<Object, EvaluateResult> e = looseRecord.data.getI(i);
      fields.add(
          new ROPair<>(
              e.first, ((MortarRecordFieldable) e.second.value.type(context)).newTupleField(i)));
    }
    return new MortarRecordType(fields);
  }

  /** Caller type needs to make sure value is read only or a duplicate before calling. */
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
      if (!(chunk instanceof MortarTargetCode)) {
        throw new Assertion();
      }
      code.add(MortarTargetCode.ex(chunk));
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
