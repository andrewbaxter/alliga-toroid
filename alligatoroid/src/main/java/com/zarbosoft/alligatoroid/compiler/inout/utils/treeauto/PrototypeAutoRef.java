package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

class PrototypeAutoRef implements Prototype {
  private final AutoTreeMeta autoTreeMeta;
  private final Class klass;

  PrototypeAutoRef(AutoTreeMeta autoTreeMeta, Class klass) {
    this.autoTreeMeta = autoTreeMeta;
    this.klass = klass;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return autoTreeMeta.infos.get(klass).create(errors, luxemPath);
  }
}
