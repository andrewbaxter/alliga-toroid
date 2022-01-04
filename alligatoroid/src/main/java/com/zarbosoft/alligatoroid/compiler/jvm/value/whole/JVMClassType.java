package com.zarbosoft.alligatoroid.compiler.jvm.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.direct.JVMMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMObjectType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.halftype.JVMStringType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeInt;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Represents the metadata for interacting with a class - inheritance, fields */
public class JVMClassType extends JVMObjectType implements AutoGraphMixin, LeafValue, SimpleValue {
  public static final String ACCESS_NEW = "new";
  public final JVMSharedNormalName jvmExternalClass;
  public final TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors;
  public final TSMap<String, JVMDataType> dataFields;
  public final TSMap<ROTuple, JVMMethodFieldType> methodFields;
  public final TSMap<String, JVMDataType> staticDataFields;
  public final TSMap<ROTuple, JVMMethodFieldType> staticMethodFields;
  public final TSList<JVMClassType> inherits;

  public final JVMSharedJVMName name;
  public final TSSet<String> fields;
  public final TSSet<String> staticFields;

  public JVMClassType(
      JVMSharedNormalName jvmExternalClass,
      TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors,
      TSMap<String, JVMDataType> dataFields,
      TSMap<ROTuple, JVMMethodFieldType> methodFields,
      TSMap<String, JVMDataType> staticDataFields,
      TSMap<ROTuple, JVMMethodFieldType> staticMethodFields,
      TSList<JVMClassType> inherits) {
    this.jvmExternalClass = jvmExternalClass;
    this.name = JVMSharedJVMName.fromNormalName(jvmExternalClass);
    this.constructors = constructors;
    this.dataFields = dataFields;
    this.methodFields = methodFields;
    fields = dataFields.keys().mut();
    this.inherits = inherits;
    for (Map.Entry<ROTuple, JVMMethodFieldType> f : methodFields) {
      fields.add((String) f.getKey().get(0));
    }
    this.staticDataFields = staticDataFields;
    this.staticMethodFields = staticMethodFields;
    staticFields = staticDataFields.keys().mut();
    for (Map.Entry<ROTuple, JVMMethodFieldType> f : staticMethodFields) {
      staticFields.add((String) f.getKey().get(0));
    }
  }

  public static JVMClassType blank(JVMSharedNormalName jvmExternalClass) {
    return new JVMClassType(
        jvmExternalClass,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
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

                @Override
                public JVMDataType handleInt(WholeInt value) {
                  return JVMIntType.value;
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

  public void resolveMethods(EvaluationContext context) {}

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field0) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    if (key.dispatch(
        new WholeValue.DefaultDispatcher<Boolean>(false) {
          @Override
          public Boolean handleString(WholeString value) {
            return ACCESS_NEW.equals(value.value);
          }
        })) {
        return EvaluateResult.pure(new JVMPseudoConstructor(this));
    }
      if (!staticFields.contains((String) key.concreteValue())) {
        context.moduleContext.errors.add(new NoField(location, key));
        return EvaluateResult.error;
      }
    return EvaluateResult.pure(new JVMPseudoStaticField(this, (String) key.concreteValue()));
  }

  @Override
  public EvaluateResult valueAccess(
      EvaluationContext context, Location location, Value field0, JVMProtocode lower) {
    WholeValue key = WholeValue.getWhole(context, location, field0);
    if (!fields.contains((String) key.concreteValue())) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new JVMPseudoField(lower, this, (String) key.concreteValue()));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.fromJVMName(name);
  }
}
