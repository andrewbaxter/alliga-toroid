package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JVMBaseClassType extends JVMObjectType {
  public final String jvmName;
  public final TSMap<String, JVMDataType> dataFields;
  public final TSMap<ROTuple, JVMShallowMethodFieldType> methodFields;
  public final TSMap<String, JVMDataType> staticDataFields;
  public final TSMap<ROTuple, JVMShallowMethodFieldType> staticMethodFields;
  public final TSSet<String> fields;
  public final TSSet<String> staticFields;
  public final TSList<JVMBaseClassType> inherits;

  public JVMBaseClassType(
      String jvmExternalClass,
      TSMap<String, JVMDataType> dataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> methodFields,
      TSMap<String, JVMDataType> staticDataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> staticMethodFields,
      TSList<JVMBaseClassType> inherits) {
    this.jvmName = JVMDescriptor.jvmName(jvmExternalClass);
    this.dataFields = dataFields;
    this.methodFields = methodFields;
    fields = dataFields.keys().mut();
    this.inherits = inherits;
    for (Map.Entry<ROTuple, JVMShallowMethodFieldType> f : methodFields) {
      fields.add((String) f.getKey().get(0));
    }
    this.staticDataFields = staticDataFields;
    this.staticMethodFields = staticMethodFields;
    staticFields = staticDataFields.keys().mut();
    for (Map.Entry<ROTuple, JVMShallowMethodFieldType> f : staticMethodFields) {
      staticFields.add((String) f.getKey().get(0));
    }
  }

  public static JVMDataType getArgTupleInner(Value value) {
    if (value instanceof WholeValue) {
      return ((WholeValue) value)
          .dispatch(
              new WholeValue.Dispatcher<JVMDataType>() {
                @Override
                public JVMDataType handleString(WholeString value) {
                  return JVMStringType.value;
                }

                @Override
                public JVMDataType handleBool(WholeBool value) {
                  return JVMBoolType.value;
                }
              });
    } else if (value instanceof JVMDataType) {
      return (JVMDataType) value;
    } else throw new Assertion();
  }

  public static ROTuple getArgTuple(Value value) {
    if (value instanceof LooseTuple) {
      List data = new ArrayList();
      for (EvaluateResult e : ((LooseTuple) value).data) {
        data.add(getArgTupleInner(e.value));
      }
      return new ROTuple(data);
    } else return ROTuple.create(getArgTupleInner(value));
  }

  public void resolveMethods(Module module) {}

  @Override
  public EvaluateResult access(Context context, Location location, Value field0) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    if (!staticFields.contains((String) key.concreteValue())) {
      context.module.log.errors.add(new Error.NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new JVMPseudoStaticField(this, (String) key.concreteValue()));
  }

  @Override
  public EvaluateResult valueAccess(
      Context context, Location location, Value field0, JVMProtocode lower) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    if (!fields.contains((String) key.concreteValue())) {
      context.module.log.errors.add(new Error.NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new JVMPseudoField(lower, this, (String) key.concreteValue()));
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.objDescriptorFromJvmName(jvmName);
  }
}
