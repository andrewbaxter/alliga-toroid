package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.inout.graph.SemideserializeSubvalueState.protoRefArtifact;
import static com.zarbosoft.alligatoroid.compiler.inout.graph.SemideserializeSubvalueState.protoRefBuiltin;

public class SemideserializeSubvalueRefState extends DefaultStateSingle<SemiserialRef> {
  private BaseStateSingle<? extends SemiserialRef> inner = null;

  public SemideserializeSubvalueRefState() {}

  @Override
  protected BaseStateSingle innerEatType(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    switch (name) {
      case SemiserialRefArtifact.SERIAL_TYPE:
        return inner = protoRefArtifact.create(errors, luxemPath);
      case SemiserialRefBuiltin.SERIAL_TYPE:
        return inner = protoRefBuiltin.create(errors, luxemPath);
      default:
        {
          errors.add(new DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public SemiserialRef build(TSList<Error> errors) {
    return inner.build(errors);
  }
}
