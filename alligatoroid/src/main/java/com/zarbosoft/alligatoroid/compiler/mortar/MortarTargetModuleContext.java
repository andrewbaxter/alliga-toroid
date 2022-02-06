package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.language.Wrap;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

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
  public static JVMSharedCode newWrapCode1 =
      new JVMSharedCode()
          .add(new TypeInsnNode(NEW, JVMDescriptorUtils.jvmName(Wrap.class)))
          .addI(DUP);
  public static JVMSharedCode newWrapCode2 =
      callConstructorCode(Wrap.class, Location.class, Value.class);
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

  public static boolean convertFunctionArgument(
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
        if (!convertFunctionArgument(context, location,pre, code, post, e.value)) {
          bad = true;
          continue;
        }
        post.add((JVMSharedCodeElement) e.postEffect);
      }
      return !bad;
    } else {
      EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      final Value variable = ectx.record(argument.vary(context, location));
      if (variable == ErrorValue.error) return false;
      code.add(((DataValue) variable).mortarVaryCode(context, location).half(context));
      final EvaluateResult prePost = ectx.build(null);
      pre.add((JVMSharedCode) prePost.preEffect);
      post.add((JVMSharedCodeElement) prePost.postEffect);
      return true;
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
        throw new Assertion();
      }
      code.add((JVMSharedCode) chunk);
    }
    return code;
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
