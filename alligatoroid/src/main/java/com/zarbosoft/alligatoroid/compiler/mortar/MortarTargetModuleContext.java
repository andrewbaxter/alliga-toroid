package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.error.IncompatibleTargetValues;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfIntType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfRecordType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfTupleType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarHalfValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeInt;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeOther;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
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

  /**
   * @param context
   * @param value
   * @return null == error
   */
  public static HalfLowerResult halfLower(EvaluationContext context, Object value) {
    if (value.getClass() == FutureValue.class) {
      return halfLower(context, ((FutureValue) value).get());
    }
    if (value == ErrorValue.error) {
      return null;

    } else if (value == NullValue.value) {
      return new HalfLowerResult(MortarHalfNullType.type, new JVMSharedCode());

    } else if (value instanceof LooseTuple) {
      JVMSharedCode out = new JVMSharedCode();
      TSList<MortarHalfDataType> types = new TSList<>();
      out.add(newTupleCode1);
      out.add(newTSListCode);
      boolean bad = false;
      for (EvaluateResult e : ((LooseTuple) value).data) {
        if (e.preEffect != null) out.add((JVMSharedCode) e.preEffect);
        HalfLowerResult lowerRes = halfLower(context, e.value);
        if (lowerRes == null) {
          bad = true;
          continue;
        }
        if (lowerRes.dataType != null) lowerRes = lowerRes.dataType.box(lowerRes.valueCode);
        types.add(lowerRes.dataType);
        out.add(lowerRes.valueCode);
        out.add(tsListAddCode);
      }
      if (bad) return null;
      out.add(newTupleCode2);
      return new HalfLowerResult(new MortarHalfTupleType(types), out);

    } else if (value instanceof LooseRecord) {
      JVMSharedCode out = new JVMSharedCode();
      TSOrderedMap<Object, MortarHalfDataType> types = new TSOrderedMap<>();
      out.add(newRecordCode1);
      out.add(newTSMapCode);
      boolean bad = false;
      for (ROPair<Object, EvaluateResult> e : ((LooseRecord) value).data) {
        if (e.second.preEffect != null) out.add((JVMSharedCode) e.second.preEffect);
        out.add(lowerRaw(context, e.first, true));
        HalfLowerResult lowerRes = halfLower(context, e.second.value);
        if (lowerRes == null) {
          bad = true;
          continue;
        }
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
      if (bad) return null;
      out.add(newRecordCode2);
      return new HalfLowerResult(new MortarHalfRecordType(types), out);

    } else if (value instanceof WholeValue) {
      return ((WholeValue) value)
          .dispatch(
              new WholeValue.Dispatcher<HalfLowerResult>() {
                @Override
                public HalfLowerResult handleString(WholeString value) {
                  return new HalfLowerResult(
                      MortarHalfStringType.type, new JVMSharedCode().addString(value.value));
                }

                @Override
                public HalfLowerResult handleBool(WholeBool value) {
                  return new HalfLowerResult(
                      MortarHalfBoolType.type, new JVMSharedCode().addBool(value.value));
                }

                @Override
                public HalfLowerResult handleInt(WholeInt value) {
                  return new HalfLowerResult(
                      MortarHalfIntType.type, new JVMSharedCode().addInt(value.value));
                }
              });

    } else if (value instanceof MortarHalfValue) {
      return new HalfLowerResult(
          ((MortarHalfValue) value).type, ((MortarHalfValue) value).mortarLower(context));

    } else {
      HalfLowerResult out = Builtin.halfLowerSingleton(value);
      if (out != null) {
        return out;
      }

      if (value instanceof WholeOther) value = ((WholeOther) value).object;
      out = Builtin.halfLowerSingleton(value);
      if (out != null) {
        return out;
      }

      return new HalfLowerResult(
          Meta.autoMortarHalfDataTypes.get(value.getClass()),
          ((MortarTargetModuleContext) context.target).transfer(value));
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
      EvaluationContext context, JVMSharedCode code, MortarValue argument) {
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        if (e.preEffect != null) code.add((JVMSharedCode) e.preEffect);
        final HalfLowerResult lowerRes = halfLower(context, e.value);
        if (lowerRes == null) continue;
        code.add(lowerRes.valueCode);
      }
    } else {
      final HalfLowerResult lowerRes = halfLower(context, argument);
      if (lowerRes == null) return;
      code.add(lowerRes.valueCode);
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

  @Override
  public ROPair<TargetCode, ? extends Binding> bind(
      EvaluationContext context, Location location, Value value) {
    if (value == ErrorValue.error) return new ROPair<>(null, ErrorBinding.binding);
    return ((MortarValue) value).mortarBind(context, location);
  }

  @Override
  public EvaluateResult call(
      EvaluationContext context, Location location, Value target, Value args) {
    if (target == ErrorValue.error || args == ErrorValue.error) return EvaluateResult.error;
    return ((MortarValue) target).mortarCall(context, location, (MortarValue) args);
  }

  @Override
  public EvaluateResult access(
      EvaluationContext context, Location location, Value target, Value field) {
    if (target == ErrorValue.error || field == ErrorValue.error) return EvaluateResult.error;
    return ((MortarValue) target).mortarAccess(context, location, (MortarValue) field);
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location, Binding binding) {
    if (binding == ErrorBinding.binding) return EvaluateResult.error;
    return ((MortarBinding) binding).mortarFork(context, location);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location, Value value) {
    if (value == ErrorValue.error) return null;
    return ((MortarValue) value).mortarDrop(context, location);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location, Binding binding) {
    if (binding == ErrorBinding.binding) return null;
    return ((MortarBinding) binding).mortarDrop(context, location);
  }

  public static class HalfLowerResult {
    public final MortarHalfDataType dataType;
    public final JVMSharedCodeElement valueCode;

    public HalfLowerResult(MortarHalfDataType dataType, JVMSharedCodeElement valueCode) {
      this.dataType = dataType;
      this.valueCode = valueCode;
    }
  }
}
