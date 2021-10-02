package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.CompilationContext;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.TSMap;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class JVMDeferredDataType implements JVMDataType, GraphSerializable {
  private final ImportSpec spec;
  private JVMDataType resolved;

  public JVMDeferredDataType(ImportSpec spec) {
    this.spec = spec;
  }

  public static JVMDeferredDataType graphDeserialize(Record data) {
    return new JVMDeferredDataType((ImportSpec) data.data.get("spec"));
  }

  private void fer(Module module) {
    if (resolved != null) return;
    CompilationContext.ImportResult importResult =
        module.compilationContext.loadModule(module.importPath, spec);
    if (importResult.error != null) {
      throw importResult.error;
    }
    resolved = (JVMDataType) ((Record) uncheck(() -> importResult.value.get())).data.get("type");
  }

  @Override
  public int storeOpcode(Module module) {
    fer(module);
    return resolved.storeOpcode(module);
  }

  @Override
  public int loadOpcode(Module module) {
    fer(module);
    return resolved.loadOpcode(module);
  }

  @Override
  public String jvmDesc(Module module) {
    fer(module);
    return resolved.jvmDesc(module);
  }

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>(s -> s.putNew("spec", spec)));
  }
}
