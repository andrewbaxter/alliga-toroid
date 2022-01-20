package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;

/** Represents the metadata for interacting with (calling) a method. */
public class JVMMethodFieldType implements  AutoBuiltinExportable, LeafExportable {
  public final String name;
  public final Record spec;
  public JVMHalfClassType base;
  public JVMUtils.MethodSpecDetails specDetails;

  public JVMMethodFieldType(JVMHalfClassType base, String name, Record spec) {
    this.base = base;
    this.name = name;
    this.spec = spec;
  }

  @Override
  public void postInit() {
    specDetails = JVMUtils.methodSpecDetails(spec);
  }
}
