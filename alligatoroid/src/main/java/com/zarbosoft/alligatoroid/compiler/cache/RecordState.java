package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateRecord;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateString;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class RecordState extends BaseStateRecord {
  private final Cache cache;
  TSList<ROPair<String, State>> children = new TSList<>();

  public RecordState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public Object build(TSList<Error> errors) {
    TSMap<Object, Object> data = new TSMap<>();
    for (ROPair<String, State> child : children) {
      data.put(child.first, child.second.build(errors));
    }
    return new Record(data);
  }

  @Override
  public BaseStateSingle createKeyState(TSList<Error> errors, LuxemPath luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(TSList<Error> errors, LuxemPath luxemPath, Object key) {
    ValueState valueState = new ValueState(cache);
    children.add(new ROPair<>((String)key, valueState));
    return valueState;
  }
}
