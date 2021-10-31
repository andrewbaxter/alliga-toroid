package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class StateObject extends BaseStateRecord {
  public final TSMap<String, State> fields = new TSMap<>();
  public final ObjectInfo info;
  private final LuxemPathBuilder luxemPath;
  private boolean ok = true;

  public StateObject(LuxemPathBuilder luxemPath, ObjectInfo info) {
    if (info.luxemType.equals("mod_local")) {
      System.out.format("");
    }
    this.info = info;
    this.luxemPath = luxemPath;
  }

  @Override
  public BaseStateSingle createKeyState(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(TSList<Error> errors, LuxemPathBuilder luxemPath, Object key0) {
    String key = (String) key0;
    if (key == null) {
      ok = false;
      return StateErrorSingle.state;
    }
    StatePrototype proto = info.fields.getOpt(key);
    if (proto == null) {
        errors.add(
                new Error.DeserializeUnknownField(luxemPath.render(), info.luxemType, key, info.fields.keys().toList()));
      ok = false;
      return StateErrorSingle.state;
    }
    BaseStateSingle state = proto.create(errors, luxemPath);
    fields.put(key, state);
    return state;
  }

  @Override
  public Object build(TSList<Error> errors) {
    if (!ok) return null;
    Object[] args = new Object[info.fields.size()];
    for (Map.Entry<String, Integer> field : info.argOrder) {
      State fieldState = fields.getOpt(field.getKey());
      if (fieldState == null) {
        ok = false;
        errors.add(new Error.DeserializeMissingField(luxemPath.render(), info.luxemType, field.getKey()));
        continue;
      }
      Object value = fieldState.build(errors);
      if (value == null) {
        ok = false;
        continue;
      }
      args[field.getValue()] = value;
    }
    if (!ok) return null;
    else return uncheck(() -> info.constructor.newInstance(args));
  }
}
