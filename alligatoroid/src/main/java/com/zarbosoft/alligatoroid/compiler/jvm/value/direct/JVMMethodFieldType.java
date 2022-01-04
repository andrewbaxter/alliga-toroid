package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

/** Represents the metadata for interacting with (calling) a method. */
public class JVMMethodFieldType implements SimpleValue, AutoGraphMixin, LeafValue {
  public final String name;
  public final Record spec;
  public JVMClassType base;
  public JVMUtils.MethodSpecDetails specDetails;

  public JVMMethodFieldType(JVMClassType base, String name, Record spec) {
    this.base = base;
    this.name = name;
    this.spec = spec;
  }

  @Override
  public void postDesemiserialize() {
    specDetails = JVMUtils.methodSpecDetails(spec);
  }
}
