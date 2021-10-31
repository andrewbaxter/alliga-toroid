package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateArrayPair;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateRecord;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateString;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class BuiltinTypeState extends DefaultStateArrayPair {
  private final Cache cache;
  private String type;
  private RecordState inner;

  public BuiltinTypeState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public BaseStateSingle createKeyState(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(TSList<Error> errors, LuxemPathBuilder luxemPath, Object key) {
    type = (String) key;
    return new DefaultStateSingle() {
      @Override
      protected BaseStateRecord innerEatRecordBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
        return inner = new RecordState(cache);
      }
    };
  }

  @Override
  public Object build(TSList<Error> errors) {
    Record record = (Record) inner.build(errors);
    return uncheck(() -> Cache.builtinTypeMap.get(type).invoke(null, record));
  }
}
