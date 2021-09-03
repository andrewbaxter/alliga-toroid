package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateRecordBegin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Paths;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class OutputTypeState extends BaseState {
  public final Cache cache;
  private String typeCacheRelPath;
  private RecordState inner;

  public OutputTypeState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    typeCacheRelPath = value;
    stack.removeLast();
    stack.add(inner = new RecordState(cache));
    stack.add(StateRecordBegin.state);
  }

  @Override
  public Object build(TSList<Error> errors) {
    cache.loadObject(errors, Paths.get(typeCacheRelPath));
    Object record = inner.build(errors);
    if (record == null) return null;
    Class typeClass;
    synchronized (cache.cacheLock) {
      typeClass = cache.loadedClasses.getOpt(typeCacheRelPath);
    }
    if (typeClass == null) return null;
    return uncheck(
        () -> typeClass.getMethod("cacheDeserialize", Record.class).invoke(null, record));
  }
}
