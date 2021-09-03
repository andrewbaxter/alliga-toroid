package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Paths;

public class ObjectState extends BaseState {
  public final Cache cache;
  private String cacheRelPath;

  public ObjectState(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    cacheRelPath = value;
    stack.removeLast();
  }

  @Override
  public Object build(TSList<Error> errors) {
    return cache.loadObject(errors, Paths.get(cacheRelPath));
  }
}
