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

import java.nio.file.Paths;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class OutputTypeState extends DefaultStateArrayPair {
  public final Cache cache;
  private String typeCacheRelPath;
  private RecordState inner;

  public OutputTypeState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public Object build(TSList<Error> errors) {
    if (inner == null) return null;
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

  @Override
  public BaseStateSingle createKeyState(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateString();
  }

  @Override
  public BaseStateSingle createValueState(TSList<Error> errors, LuxemPathBuilder luxemPath, Object key) {
    typeCacheRelPath = (String) key;
    return new DefaultStateSingle() {
      @Override
      protected BaseStateRecord innerEatRecordBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
        return inner = new RecordState(cache);
      }
    };
  }
}
