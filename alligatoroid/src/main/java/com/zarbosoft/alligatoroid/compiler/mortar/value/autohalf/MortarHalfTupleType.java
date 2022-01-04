package com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeInt;
import com.zarbosoft.rendaw.common.TSList;

public class MortarHalfTupleType extends MortarHalfObjectType {
  public static final JVMSharedJVMName JVMNAME = JVMSharedJVMName.fromClass(Tuple.class);
  public static final JVMSharedDataDescriptor DESC = JVMSharedDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarHalfDataType> fields;

  public MortarHalfTupleType(TSList< MortarHalfDataType> fields) {
    this.fields = fields;
  }

  @Override
  public Value unlower(Object object) {
    Tuple source = (Tuple) object;
    final TSList< EvaluateResult> data = new TSList<>();
    for (int i = 0; i < source.data.size(); i++) {
      final MortarHalfDataType field = fields.get(i);
      final Object element = source.data.get(i);
      data.add(
              EvaluateResult.pure(field.unlower(element)));
    }
    return new LooseTuple(data);
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }

  @Override
  public EvaluateResult valueAccess(
      EvaluationContext context, Location location, Value field0, MortarProtocode lower) {
      if (field0 == ErrorValue.error) return EvaluateResult.error;
      if (!(field0 instanceof WholeInt)) {
          context.moduleContext.errors.add(new WrongType(location, field0, "int"));
      }
      WholeInt key = (WholeInt) field0;
      if (key.value < 0 || key.value >= fields.size()) {
          context.moduleContext.errors.add(new NoField(location, key));
          return EvaluateResult.error;
      }
    MortarHalfDataType field = fields.get(key.concreteValue());
    return EvaluateResult.pure(
        field.asValue(
                location, new MortarProtocode() {
              @Override
              public JVMSharedCodeElement lower(EvaluationContext context) {
                JVMSharedCode out = new JVMSharedCode();
                out.add(lower.lower(context));
                out.add(
                    JVMSharedCode.callMethod(
                        context.sourceLocation(location),
                        JVMNAME,
                        "get",
                        JVMSharedFuncDescriptor.fromParts(
                            JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.INT)));
                out.add(JVMSharedCode.cast(field.jvmDesc()));
                return out;
              }

              @Override
              public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }
}
