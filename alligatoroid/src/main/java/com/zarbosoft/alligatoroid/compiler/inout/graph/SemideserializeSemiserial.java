package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateArrayBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateArray;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeMissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class SemideserializeSemiserial extends BaseStateRecordBody<Void, SemiserialModule> {
  private final LuxemPathBuilder luxemPath;
  private SemideserializeSubvalueRefState root;
  private StateArray<Void, ROList<SemiserialValue>> artifacts;

  public SemideserializeSemiserial(LuxemPathBuilder luxemPath) {
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
      case SemiserialModule.KEY_ROOT:
        return root = new SemideserializeSubvalueRefState();
      case SemiserialModule.KEY_ARTIFACTS:
        return artifacts =
            new StateArray<Void, ROList<SemiserialValue>>(
                new DefaultStateArrayBody<Void, ROList<SemiserialValue>>() {
                  public final TSList<SemideserializeSemiserialValue> elements = new TSList<>();

                  @Override
                  public ROList<SemiserialValue> build(Void context, TSList<Error> errors) {
                    final TSList<SemiserialValue> out = new TSList<>();
                    for (SemideserializeSemiserialValue element : elements) {
                      SemiserialValue v = element.build(context, errors);
                      if (v == null) return null;
                      out.add(v);
                    }
                    return out;
                  }

                  @Override
                  public BaseStateSingle createElementState(
                      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
                    final SemideserializeSemiserialValue out =
                        new SemideserializeSemiserialValue(luxemPath);
                    elements.add(out);
                    return new StateRecord(out);
                  }
                });
      default:
        {
          errors.add(new DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public SemiserialModule build(Void context, TSList<Error> errors) {
    if (root == null) {
      errors.add(
          new DeserializeMissingField(luxemPath.render(), "semiserial", SemiserialModule.KEY_ROOT));
      return null;
    }
    if (artifacts == null) {
      errors.add(
          new DeserializeMissingField(
              luxemPath.render(), "semiserial", SemiserialModule.KEY_ARTIFACTS));
      return null;
    }
    final SemiserialRef rootRes = root.build(context, errors);
    final ROList<SemiserialValue> artifactsRes = artifacts.build(context, errors);
    return new SemiserialModule(rootRes, artifactsRes);
  }
}
