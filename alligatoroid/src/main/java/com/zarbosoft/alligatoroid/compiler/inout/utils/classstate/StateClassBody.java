package com.zarbosoft.alligatoroid.compiler.inout.utils.classstate;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.State;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeMissingField;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownField;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class StateClassBody<C, T> extends BaseStateRecordBody<C, T> {
  public final TSMap<String, State> fields = new TSMap<>();
  public final ClassInfo info;
  private final LuxemPathBuilder luxemPath;
  private boolean ok = true;

  public StateClassBody(LuxemPathBuilder luxemPath, ClassInfo info) {
    this.info = info;
    this.luxemPath = luxemPath;
  }

  @Override
  public BaseStateSingle createKeyState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, Object key0) {
    String key = (String) key0;
    if (key == null) {
      ok = false;
      return StateErrorSingle.state;
    }
    Prototype proto = info.fields.getOpt(key);
    if (proto == null) {
      errors.add(
          new DeserializeUnknownField(
              luxemPath.render(), info.luxemType, key, info.fields.keys().toList()));
      ok = false;
      return StateErrorSingle.state;
    }
    BaseStateSingle state = proto.create(errors, luxemPath);
    fields.put(key, state);
    return state;
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    if (!ok) return null;
    Object[] args = new Object[info.fields.size()];
    for (Map.Entry<String, Integer> field : info.argOrder) {
      State fieldState = fields.getOpt(field.getKey());
      if (fieldState == null) {
        ok = false;
        errors.add(new DeserializeMissingField(luxemPath.render(), info.luxemType, field.getKey()));
        continue;
      }
      Object value = fieldState.build(context, errors);
      if (value == null) {
        ok = false;
        continue;
      }
      args[field.getValue()] = value;
    }
    if (!ok) return null;
    else return (T) uncheck(() -> info.constructor.newInstance(args));
  }
}
