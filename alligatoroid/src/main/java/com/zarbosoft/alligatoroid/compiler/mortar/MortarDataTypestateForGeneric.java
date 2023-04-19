package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface MortarDataTypestateForGeneric extends MortarDataTypestate {
  @Override
  MortarDataTypeForGeneric typestate_asType();

  @Override
  MortarDataTypestateForGeneric typestate_fork();

  @Override
  MortarDataTypestateForGeneric typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation);
}
