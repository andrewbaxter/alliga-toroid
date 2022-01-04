package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.error.IncompatibleTargetValues;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.MortarHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.MortarHalfRecordType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.MortarHalfStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.MortarHalfTupleType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.half.MortarHalfValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement.JVM_TARGET_NAME;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;

public class MortarTargetModuleContext implements TargetModuleContext {
  public static final String TRANSFER_PREFIX = "transfer";
  public static JVMSharedCode newTSListCode =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(TSList.class)))
          .addI(DUP)
          .add(callConstructorCode(TSList.class));
  public static JVMSharedCode tsListAddCode =
      new JVMSharedCode()
          .add(
              new MethodInsnNode(
                  INVOKEVIRTUAL,
                  JVMDescriptorUtils.jvmName(TSList.class),
                  "add",
                  JVMDescriptorUtils.func(
                      JVMDescriptorUtils.objDescriptorFromReal(TSList.class),
                      JVMDescriptorUtils.objDescriptorFromReal(Object.class)),
                  false));
  public static JVMSharedCode newTSMapCode =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(TSMap.class)))
          .addI(DUP)
          .add(callConstructorCode(TSMap.class));
  public static JVMSharedCode newTupleCode1 =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(Tuple.class)))
          .addI(DUP);
  public static JVMSharedCode newTupleCode2 = callConstructorCode(Tuple.class, ROList.class);
  public static JVMSharedCode newRecordCode1 =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(Record.class)))
          .addI(DUP);
  public static JVMSharedCode newRecordCode2 = callConstructorCode(Record.class, ROMap.class);
  public static JVMSharedCode newLocationCode1 =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(Location.class)))
          .addI(DUP);
  public static JVMSharedCode newLocationCode2 =
      callConstructorCode(Location.class, ModuleId.class, int.class);
  public final TSOrderedMap<Object, String> transfers = new TSOrderedMap<>();
  public final String moduleInternalName;

  public MortarTargetModuleContext(String moduleInternalName) {
    this.moduleInternalName = moduleInternalName;
  }

  public static JVMSharedCode callConstructorCode(Class klass, Class... args) {
    String[] argDesc = new String[args.length];
    for (int i = 0; i < args.length; i++) {
      Class arg = args[i];
      if (arg == int.class) {
        argDesc[i] = JVMDescriptorUtils.INT_DESCRIPTOR;
        continue;
      }
      if (arg.isPrimitive()) throw new Assertion(); // todo?
      if (arg.isArray()) throw new Assertion();
      argDesc[i] = JVMDescriptorUtils.objDescriptorFromReal(arg);
    }
    return new JVMSharedCode()
        .add(
            new MethodInsnNode(
                INVOKESPECIAL,
                JVMDescriptorUtils.jvmName(klass),
                "<init>",
                JVMDescriptorUtils.func("V", argDesc),
                false));
  }

  public static LowerResult lower(EvaluationContext context, Value value) {
    if (value.getClass() == FutureValue.class) {
      return lower(context, ((FutureValue) value).get());
    }
    if (value == NullValue.value) {
      return new LowerResult(NullType.type, new JVMSharedCode());

    } else if (value instanceof LooseTuple) {
      JVMSharedCode out = new JVMSharedCode();
      TSList<MortarHalfDataType> types = new TSList<>();
      out.add(newTupleCode1);
      out.add(newTSListCode);
      for (EvaluateResult e : ((LooseTuple) value).data) {
        if (e.preEffect != null) out.add((JVMSharedCode) e.preEffect);
        LowerResult lowerRes = lower(context, e.value);
        if (lowerRes.dataType != null) lowerRes = lowerRes.dataType.box(lowerRes.valueCode);
        types.add(lowerRes.dataType);
        out.add(lowerRes.valueCode);
        out.add(tsListAddCode);
      }
      out.add(newTupleCode2);
      return new LowerResult(new MortarHalfTupleType(types), out);

    } else if (value instanceof LooseRecord) {
      JVMSharedCode out = new JVMSharedCode();
      TSOrderedMap<Object, MortarHalfDataType> types = new TSOrderedMap<>();
      out.add(newRecordCode1);
      out.add(newTSMapCode);
      for (ROPair<Object, EvaluateResult> e : ((LooseRecord) value).data) {
        if (e.second.preEffect != null) out.add((JVMSharedCode) e.second.preEffect);
        out.add(lowerRaw(context, e.first, true));
        LowerResult lowerRes = lower(context, e.second.value);
        if (lowerRes.dataType != null) lowerRes = lowerRes.dataType.box(lowerRes.valueCode);
        types.put(e.first, lowerRes.dataType);
        out.add(lowerRes.valueCode);
        out.add(
            new MethodInsnNode(
                INVOKEVIRTUAL,
                JVMDescriptorUtils.jvmName(TSMap.class),
                "put",
                JVMDescriptorUtils.func(
                    JVMDescriptorUtils.objDescriptorFromReal(TSMap.class),
                    JVMDescriptorUtils.objDescriptorFromReal(Object.class),
                    JVMDescriptorUtils.objDescriptorFromReal(Object.class)),
                false));
      }
      out.add(newRecordCode2);
      return new LowerResult(new MortarHalfRecordType(types), out);

    } else if (value instanceof WholeString) {
      return new LowerResult(
          MortarHalfStringType.type, new JVMSharedCode().addString(((WholeString) value).value));

    } else if (value instanceof WholeBool) {
      return new LowerResult(
          MortarHalfBoolType.type, new JVMSharedCode().addBool(((WholeBool) value).value));

    } else if (value instanceof MortarHalfValue) {
      return new LowerResult(((MortarHalfValue) value).type, ((MortarHalfValue) value).lower(context));

    } else {
      if (value instanceof WholeValue) throw new Assertion();
      return new LowerResult(
          null /* TODO */, ((MortarTargetModuleContext) context.target).transfer(value));
    }
  }

  public static JVMSharedCodeElement lowerRaw(
      EvaluationContext context, Object value, boolean boxed) {
    if (value.getClass() == String.class) {
      return new JVMSharedCode().addString((String) value);
    } else if (value.getClass() == Integer.class && !boxed) {
      return new JVMSharedCode().addInt((Integer) value);
    } else if (value.getClass() == Location.class) {
      JVMSharedCode out = new JVMSharedCode();
      out.add(MortarTargetModuleContext.newLocationCode1);
      Location location = (Location) value;
      out.add(((MortarTargetModuleContext) context.target).transfer(location.module));
      out.addInt(location.id);
      out.add(MortarTargetModuleContext.newLocationCode2);
      return out;
    } else throw new Assertion();
  }

  public static void convertFunctionArgument(
      EvaluationContext context, JVMSharedCode code, Value argument) {
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        if (e.preEffect != null) code.add((JVMSharedCode) e.preEffect);
        code.add(lower(context, e.value).valueCode);
      }
    } else {
      code.add(lower(context, argument).valueCode);
    }
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
                    JVMDescriptorUtils.objDescriptorFromReal(object.getClass())));
  }

  @Override
  public JVMSharedCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> chunks) {
    JVMSharedCode code = new JVMSharedCode();
    for (TargetCode chunk : chunks) {
      if (chunk == null) continue;
      if (!(chunk instanceof JVMSharedCode)) {
        context.moduleContext.errors.add(
            new IncompatibleTargetValues(location, JVM_TARGET_NAME, chunk.targetName()));
        return null;
      }
      code.add((JVMSharedCode) chunk);
    }
    return code;
  }

  public static class LowerResult {
    public final MortarHalfDataType dataType;
    public final JVMSharedCodeElement valueCode;

    public LowerResult(MortarHalfDataType dataType, JVMSharedCodeElement valueCode) {
      this.dataType = dataType;
      this.valueCode = valueCode;
    }
  }
}
