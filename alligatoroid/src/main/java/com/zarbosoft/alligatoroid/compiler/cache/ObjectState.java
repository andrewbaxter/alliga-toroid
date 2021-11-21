package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Paths;

public class ObjectState extends DefaultStateSingle {
  public final Cache cache;
  private String cacheRelPath;

  public ObjectState(Cache cache) {
    this.cache = cache;
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    cacheRelPath = value;
  }

  @Override
  public Object build(TSList<Error> errors) {
    return cache.loadObject(errors, Paths.get(cacheRelPath));
  }
}
