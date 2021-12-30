package com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class PrototypeLocation implements Prototype {
  public PrototypeLocation() {
  }

  @Override
  public BaseStateSingle<ModuleId, Location> create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new DefaultStateInt<ModuleId, Location>() {
      @Override
      public Location build(ModuleId module, TSList<Error> errors1) {
        if (value == null) {
          return null;
        }
        return new Location(module, value);
      }
    };
  }
}
