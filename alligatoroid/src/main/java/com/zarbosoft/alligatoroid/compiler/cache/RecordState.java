package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class RecordState extends BaseState {
  private final Cache cache;
  TSList<ROPair<String, State>> children = new TSList<>();

  public RecordState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void eatKey(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
    ValueState state = new ValueState(cache);
    children.add(new ROPair<>(name, state));
    stack.add(state);
  }

  @Override
  public void eatRecordEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    stack.removeLast();
  }

  @Override
  public Object build(TSList<Error> errors) {
    TSMap<Object, Object> data = new TSMap<>();
    for (ROPair<String, State> child : children) {
      data.put(child.first, child.second.build(errors));
    }
    return new Record(data);
  }
}
