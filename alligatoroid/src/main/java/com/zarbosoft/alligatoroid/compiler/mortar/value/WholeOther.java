package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;

public class WholeOther
    implements SimpleValue, TreeDumpable, AutoBuiltinExportable, LeafExportable {
  public Object object;

  public WholeOther(Object object) {
    if (object instanceof Value) throw new Assertion();
    this.object = object;
  }

  @Override
  public void treeDump(Writer writer) {
    writer.type("other").primitive(object.toString());
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return Meta.autoMortarHalfDataTypes
        .get(object.getClass())
        .asValue(
            location,
            new MortarProtocode() {
              @Override
              public JVMSharedCodeElement lower(EvaluationContext context) {
                return ((MortarTargetModuleContext) context.target).transfer(object);
              }

              @Override
              public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
                return null;
              }
            })
        .access(context, location, field);
  }
}
