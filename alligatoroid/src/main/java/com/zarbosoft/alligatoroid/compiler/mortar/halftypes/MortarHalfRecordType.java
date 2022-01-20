package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class MortarHalfRecordType extends MortarHalfObjectType
    implements AutoBuiltinExportable, LeafExportable {
  public static final JVMSharedJVMName JVMNAME = JVMSharedJVMName.fromClass(Record.class);
  public static final JVMSharedDataDescriptor DESC = JVMSharedDataDescriptor.fromJVMName(JVMNAME);
  public final TSOrderedMap<Object, MortarHalfDataType> fields;

  public MortarHalfRecordType(TSOrderedMap<Object, MortarHalfDataType> fields) {
    this.fields = fields;
  }

  @Override
  public MortarValue unlower(Object object) {
    Record source = (Record) object;
    final TSOrderedMap<Object, EvaluateResult> data = new TSOrderedMap<>();
    for (ROPair<Object, MortarHalfDataType> field : fields) {
      data.put(
          field.first, EvaluateResult.pure(field.second.unlower(source.data.get(field.first))));
    }
    return new LooseRecord(data);
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }

  @Override
  public EvaluateResult valueAccess(
          EvaluationContext context, Location location, MortarValue field0, MortarProtocode lower) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    MortarHalfDataType field = fields.getOpt(key.concreteValue());
    if (field == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(
        field.asValue(
            location,
            new MortarProtocode() {
              @Override
              public JVMSharedCodeElement mortarHalfLower(EvaluationContext context) {
                JVMSharedCode out = new JVMSharedCode();
                out.add(lower.mortarHalfLower(context));
                out.add(
                    JVMSharedCode.callMethod(
                        context.sourceLocation(location),
                        JVMNAME,
                        "get",
                        JVMSharedFuncDescriptor.fromParts(
                            JVMSharedDataDescriptor.OBJECT, JVMSharedDataDescriptor.OBJECT)));
                out.add(JVMSharedCode.cast(field.jvmDesc()));
                return out;
              }

              @Override
              public JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }
}
