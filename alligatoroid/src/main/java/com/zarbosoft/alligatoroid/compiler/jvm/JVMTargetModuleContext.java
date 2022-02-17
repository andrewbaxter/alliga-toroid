package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMDataValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongTarget;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarBoolType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarIntType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStringType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

import java.util.ArrayList;
import java.util.List;

public class JVMTargetModuleContext implements TargetModuleContext {
  public static final Id ID =
      new Id() {
        @Override
        public String toString() {
          return "jvm";
        }
      };

  public static void convertFunctionRootArgument(
      EvaluationContext context,
      Location location,
      JVMSharedCode pre,
      JVMSharedCode code,
      JVMSharedCode post,
      Value argument) {
    if (argument instanceof LooseTuple) {
      for (EvaluateResult e : ((LooseTuple) argument).data) {
        pre.add((JVMSharedCode) e.preEffect);
        code.add(convertFunctionArgument(context, location, e.value));
        post.add((JVMSharedCode) e.postEffect);
      }
    } else {
      code.add(convertFunctionArgument(context, location, argument));
    }
  }

  public static JVMSharedCodeElement convertFunctionArgument(
      EvaluationContext context, Location location, Value value) {
    if (value instanceof ConstDataValue) {
      if (MortarIntType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMSharedCode.int_((Integer) ((ConstDataValue) value).getInner());
      } else if (MortarStringType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMSharedCode.string((String) ((ConstDataValue) value).getInner());
      } else if (MortarBoolType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMSharedCode.bool_((Boolean) ((ConstDataValue) value).getInner());
      } else throw new Assertion();
    } else if (value instanceof JVMDataValue) {
      final JVMProtocode code = ((JVMDataValue) value).jvmCode(context, location);
      if (code == null) return null;
      return code.code(context);
    } else throw new Assertion();
  }

  public static boolean assertTarget(EvaluationContext context, Location location) {
    if (context.target.getClass() != MortarTargetModuleContext.class) {
      context.moduleContext.errors.add(new WrongTarget(location, ID, context.target.id()));
      return false;
    }
    return true;
  }

  public static JVMType correspondJvmType(Value value) {
    if (value instanceof ConstDataValue) {
      if (MortarIntType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMIntType.type;
      } else if (MortarStringType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMStringType.type;
      } else if (MortarBoolType.type.checkAssignableFrom(
          new TSList<>(), null, ((ConstDataValue) value).mortarType(), new TSList<>())) {
        return JVMBoolType.type;
      } else throw new Assertion();
    } else if (value instanceof JVMDataValue) {
      return ((JVMDataValue) value).jvmType();
    } else throw new Assertion();
  }

  public static ROTuple correspondJvmTypeTuple(Value value) {
    if (value instanceof LooseTuple) {
      List data = new ArrayList();
      for (EvaluateResult e : ((LooseTuple) value).data) {
        data.add(correspondJvmType(e.value));
      }
      return new ROTuple(data);
    } else return ROTuple.create(correspondJvmType(value));
  }

  @Override
  public boolean codeEmpty(TargetCode code) {
    return JVMSharedCodeElement.empty((JVMSharedCodeElement) code);
  }

  @Override
  public TargetCode merge(
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

  @Override
  public Id id() {
    return ID;
  }
}
