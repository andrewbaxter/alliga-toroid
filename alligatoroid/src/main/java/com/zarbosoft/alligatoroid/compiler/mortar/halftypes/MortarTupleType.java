package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.TSList;

public class MortarTupleType extends MortarObjectType
    implements AutoBuiltinExportable, LeafExportable {
  public static final JVMSharedJVMName JVMNAME = JVMSharedJVMName.fromClass(Tuple.class);
  public static final JVMSharedDataDescriptor DESC = JVMSharedDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarDataType> fields;

  public MortarTupleType(TSList<MortarDataType> fields) {
    this.fields = fields;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry targetCarry, Value field0) {
    if (field0 == ErrorValue.error) return EvaluateResult.error;
    if (!(field0 instanceof ConstInt)) {
      context.moduleContext.errors.add(new WrongType(location, field0, "int"));
    }
    ConstInt key = (ConstInt) field0;
    if (key.value < 0 || key.value >= fields.size()) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    MortarDataType field = fields.get(key.concreteValue());
    JVMSharedCode out = new JVMSharedCode();
    out.add(targetCarry.half(context));
    out.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location),
            JVMNAME,
            "get",
            JVMSharedFuncDescriptor.fromParts(
                JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.INT)));
    out.add(JVMSharedCode.cast(field.jvmDesc()));
    return EvaluateResult.pure(field.deferredStackAsValue(out));
  }
}
