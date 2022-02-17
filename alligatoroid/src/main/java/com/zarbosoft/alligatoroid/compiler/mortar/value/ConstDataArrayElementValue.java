package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

import java.lang.reflect.Array;

public class ConstDataArrayElementValue extends ConstDataValue {
  private final int index;
  private final Object array;
  private final MortarDataType elementType;

  public ConstDataArrayElementValue(MortarDataType elementType, Object array, int index) {
    this.elementType = elementType;
    this.array = array;
    this.index = index;
  }

  @Override
  public MortarDataType mortarType() {
    return elementType;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(c -> elementType.constValueVary(context, getInner()));
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return setHelper(
        this,
        context,
        location,
        value,
        v -> Array.set(array, index, ((ConstDataValue) value).getInner()));
  }

  @Override
  public Object getInner() {
    return Array.get(array, index);
  }
}
