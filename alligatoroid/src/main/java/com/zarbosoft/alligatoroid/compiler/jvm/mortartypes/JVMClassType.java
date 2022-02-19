package com.zarbosoft.alligatoroid.compiler.jvm.mortartypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMPseudoFieldMeta;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMPseudoStaticFieldValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectType;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstString;

public class JVMClassType extends MortarObjectType implements SingletonBuiltinExportable {
  public static final JVMClassType type = new JVMClassType();
  public static final String ACCESS_NEW = "new";
  private static final JVMSharedDataDescriptor DESC =
      JVMSharedDataDescriptor.fromObjectClass(JVMClassInstanceType.class);

  private JVMClassType() {}

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location, Object inner) {
    return ((JVMClassInstanceType) inner).traceStaticFields(context, location);
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    final JVMClassInstanceType type = (JVMClassInstanceType) value;
    if (!type.resolveInternals(context, location)) return EvaluateResult.error;
    String key = assertConstString(context, location, field0);
    if (key == null) return EvaluateResult.error;
    if (ACCESS_NEW.equals(key)) {
      return EvaluateResult.pure(JVMConstructorType.type.constAsValue(type));
    }
    final JVMPseudoFieldMeta field = type.staticFields.getOpt(key);
    if (field == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(JVMPseudoStaticFieldValue.createJVMPseudoStaticFieldValue(field));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }
}
