package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.BuiltinExportableType;
import com.zarbosoft.rendaw.common.ROList;

/** Like a.b.c */
public class JVMSharedNormalName implements LeafExportable {
  public static final BuiltinExportableType exportableType =
      new BuiltinExportableType() {
        @Override
        public Exportable graphDesemiserializeChild(
            ModuleCompileContext context,
            Desemiserializer typeDesemiserializer,
            SemiserialSubvalue data) {
          return data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Exportable handleString(SemiserialString s) {
                  return new JVMSharedNormalName(s.value);
                }
              });
        }
      };
  public final String value;

  private JVMSharedNormalName(String value) {
    this.value = value;
  }

  public static JVMSharedNormalName fromString(String name) {
    return new JVMSharedNormalName(name);
  }

  public String toString() {
    return value;
  }

  @Override
  public SemiserialSubvalue graphSemiserialize(
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return new SemiserialString(value);
  }

  @Override
  public void postDesemiserialize() {}

  @Override
  public Exportable type() {
    return exportableType;
  }
}
