package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleId;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class LocationPrototype implements StatePrototype {
  private final ModuleId module;

  public LocationPrototype(ModuleId module) {
    this.module = module;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateInt() {
      @Override
      public Object build(TSList<Error> errors1) {
        Integer value = (Integer) super.build(errors1);
        if (value == null) {
          return null;
        }
        return new Location(module, value);
      }
    };
  }
}
