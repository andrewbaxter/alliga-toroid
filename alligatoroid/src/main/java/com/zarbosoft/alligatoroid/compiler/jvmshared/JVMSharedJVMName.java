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

/** Like a/b/c */
public class JVMSharedJVMName implements LeafExportable {
  public static final JVMSharedJVMName BOOL = fromClass(Boolean.class);
  public static final JVMSharedJVMName BYTE = fromClass(Byte.class);
  public static final JVMSharedJVMName INT = fromClass(Integer.class);
  public static final JVMSharedJVMName OBJECT = fromClass(Object.class);
  public static final JVMSharedJVMName STRING = fromClass(String.class);
  public final String value;
  public final static BuiltinExportableType exportableType =
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
                  return new JVMSharedJVMName(s.value);
                }
              });
        }
      };

  private JVMSharedJVMName(String value) {
    this.value = value;
  }

  public static JVMSharedJVMName fromNormalName(JVMSharedNormalName name) {
    return new JVMSharedJVMName(name.value.replace('.', '/'));
  }

  public static JVMSharedJVMName fromClass(Class klass) {
    return new JVMSharedJVMName(fromClassInternal(klass));
  }

  private static String fromClassInternal(Class klass) {
    Class enclosing = klass.getNestHost();
    if (enclosing == klass) return klass.getCanonicalName().replace('.', '/');
    else {
      return fromClassInternal(enclosing) + "$" + klass.getSimpleName();
    }
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
