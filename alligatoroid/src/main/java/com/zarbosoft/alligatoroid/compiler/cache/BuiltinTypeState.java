package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateRecordBegin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class BuiltinTypeState extends BaseState {
  private final Cache cache;
  private String type;
  private RecordState inner;

  public BuiltinTypeState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    type = value;
    stack.removeLast();
    stack.add(inner = new RecordState(cache));
    stack.add(StateRecordBegin.state);
  }

  @Override
  public Object build(TSList<Error> errors) {
    Record record = (Record) inner.build(errors);
    return uncheck(() -> Cache.builtinTypeMap.get(type).invoke(null, record));
  }
}
