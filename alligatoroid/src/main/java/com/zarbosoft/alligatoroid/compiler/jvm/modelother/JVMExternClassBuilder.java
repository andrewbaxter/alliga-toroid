package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMExternClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMNullType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin.nullType;

public class JVMExternClassBuilder {
  public final JVMExternClassInstanceType base;

  public JVMExternClassBuilder(JVMExternClassInstanceType base) {
    this.base = base;
  }

  public static JVMSoftType convertType(Object e) {
    if (e instanceof JVMSoftType)
      return (JVMSoftType) e;
    if (e == nullType || e == MortarImmutableType.nullType)
      e = JVMNullType.type;
    if (e instanceof JVMType) return JVMSoftTypeType.create((JVMType) e);
    return JVMSoftTypeDeferred.create((ImportId) e);
  }

  private static TSList<JVMSoftType> convertArguments(Record spec) {
    Object inRaw0 = spec.data.get("in");
    final TSList<JVMSoftType> inRaw = new TSList<>();
    if (inRaw0 instanceof Tuple) {
      for (Object e : ((Tuple) inRaw0).data) {
        inRaw.add(convertType(e));
      }
    } else {
      inRaw.add(convertType(inRaw0));
    }
    return inRaw;
  }

  @Meta.WrapExpose
  public void inherit(ImportId type) {
    base.softInherits.add(type);
  }

  @Meta.WrapExpose
  public void constructor(Record spec) {
    final TSList<JVMSoftType> inRaw = convertArguments(spec);
    base.softConstructors.add(
        JVMExternClassInstanceType.SoftConstructor.create(
            inRaw, JVMUtils.methodSpecDetailsAttributes(spec)));
  }

  @Meta.WrapExpose
  public void method(String name, Record spec) {
    JVMUtils.MethodSpecDetailsAttributes attributes = JVMUtils.methodSpecDetailsAttributes(spec);
    Object outRaw0 = spec.data.get("out");
    final TSList<JVMSoftType> inRaw = convertArguments(spec);
    final JVMExternClassInstanceType.SoftMethodField method =
        JVMExternClassInstanceType.SoftMethodField.create(convertType(outRaw0), inRaw, attributes);
    if (attributes.isStatic) {
      base.softStaticFields
          .getCreate(name, () -> JVMExternClassInstanceType.SoftField.blank())
          .methods
          .add(method);
    } else {
      base.softFields
          .getCreate(name, () -> JVMExternClassInstanceType.SoftField.blank())
          .methods
          .add(method);
    }
  }

  @Meta.WrapExpose
  public void data(String name, Record spec) {
    JVMUtils.DataSpecDetailsAttributes attributes = JVMUtils.dataSpecDetailsAttributes(spec);
    JVMSoftType type = convertType(spec.data.get("type"));
    if (attributes.isStatic) {
      base.softStaticFields.getCreate(name, () -> JVMExternClassInstanceType.SoftField.blank())
              .data =
          JVMExternClassInstanceType.SoftDataField.create(type, attributes);
    } else {
      base.softFields.getCreate(name, () -> JVMExternClassInstanceType.SoftField.blank()).data =
          JVMExternClassInstanceType.SoftDataField.create(type, attributes);
    }
  }
}
