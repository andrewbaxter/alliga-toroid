package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeMissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class SemideserializeSemiserialValue extends BaseStateRecordBody<Void, SemiserialValue> {
  private final LuxemPathBuilder luxemPath;
  private SemideserializeSubvalueRefState type;
  private SemideserializeSubvalueState data;

  public SemideserializeSemiserialValue(LuxemPathBuilder luxemPath) {
    this.luxemPath = luxemPath;
  }

  @Override
  public BaseStateSingle createKeyState(
      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(
      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath, Object key0) {
    String name = (String) key0;
    switch (name) {
      case SemiserialValue.KEY_TYPE:
        return type = new SemideserializeSubvalueRefState();
      case SemiserialValue.KEY_DATA:
        return data = new SemideserializeSubvalueState();
      default:
        {
          errors.add(new DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public SemiserialValue build(Void context, TSList<Error> errors) {
    if (type == null) {
      errors.add(
          new DeserializeMissingField(
              luxemPath.render(), "semiserial value", SemiserialValue.KEY_TYPE));
      return null;
    }
    if (data == null) {
      errors.add(
          new DeserializeMissingField(
              luxemPath.render(), "semiserial value", SemiserialValue.KEY_DATA));
      return null;
    }
    final SemiserialRef typeRes = type.build(context, errors);
    final SemiserialSubvalue dataRes = data.build(context, errors);
    return new SemiserialValue(typeRes, dataRes);
  }
}
