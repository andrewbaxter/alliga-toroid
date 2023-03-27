package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessTupleField;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarTupleFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataConstValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataVariableValue;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public interface MortarSimpleDataType
    extends MortarTupleFieldType, MortarDataType, BuiltinSingletonExportable {
  @Override
  default EvaluateResult tuple_fieldtype_constAsValue(
      EvaluationContext context, Location location, Object base, int key) {
    return EvaluateResult.pure(MortarDataConstValue.create(this, ((Tuple) base).get(key)));
  }

  @Override
  default EvaluateResult tuple_fieldtype_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode, int field) {
    return EvaluateResult.pure(
        new MortarDataVariableValue(
            this,
            new MortarDeferredCodeAccessTupleField(baseCode, field, tuple_fieldtype_jvmDesc())));
  }

  @Override
  default MortarTupleFieldType tuple_fieldtype_fork() {
    return this;
  }
}
