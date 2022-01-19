package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfObjectType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfStringType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.ModuleError;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeInt;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Represents the metadata for interacting with a class - inheritance, fields */
public class JVMHalfClassType extends JVMHalfObjectType
    implements AutoBuiltinExportable, SimpleValue, LeafExportable {
  public static final String ACCESS_NEW = "new";
  public final TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors;
  public final TSMap<String, JVMHalfDataType> dataFields;
  public final TSMap<ROTuple, JVMMethodFieldType> methodFields;
  public final TSMap<String, JVMHalfDataType> staticDataFields;
  public final TSMap<ROTuple, JVMMethodFieldType> staticMethodFields;
  public final TSList<JVMHalfClassType> inherits;
  public JVMSharedJVMName jvmName;
  public TSSet<String> fields;
  public TSSet<String> staticFields;
  public JVMSharedNormalName name;
  public boolean error = false;

  public JVMHalfClassType(
      JVMSharedNormalName name,
      TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors,
      TSMap<String, JVMHalfDataType> dataFields,
      TSMap<ROTuple, JVMMethodFieldType> methodFields,
      TSMap<String, JVMHalfDataType> staticDataFields,
      TSMap<ROTuple, JVMMethodFieldType> staticMethodFields,
      TSList<JVMHalfClassType> inherits) {
    this.name = name;
    this.constructors = constructors;
    this.dataFields = dataFields;
    this.methodFields = methodFields;
    this.inherits = inherits;
    this.staticDataFields = staticDataFields;
    this.staticMethodFields = staticMethodFields;
  }

  public static JVMHalfClassType blank(JVMSharedNormalName jvmExternalClass) {
    final JVMHalfClassType out =
        new JVMHalfClassType(
            jvmExternalClass,
            new TSMap<>(),
            new TSMap<>(),
            new TSMap<>(),
            new TSMap<>(),
            new TSMap<>(),
            new TSList<>());
    out.postInit();
    return out;
  }

  public static JVMHalfDataType getArgTupleInner(Value value) {
    if (value instanceof WholeValue) {
      return ((WholeValue) value)
          .dispatch(
              new WholeValue.Dispatcher<JVMHalfDataType>() {
                @Override
                public JVMHalfDataType handleString(WholeString value) {
                  return JVMHalfStringType.value;
                }

                @Override
                public JVMHalfDataType handleBool(WholeBool value) {
                  return JVMHalfBoolType.value;
                }

                @Override
                public JVMHalfDataType handleInt(WholeInt value) {
                  return JVMHalfIntType.value;
                }
              });
    } else if (value instanceof JVMHalfDataType) {
      return (JVMHalfDataType) value;
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

  @Override
  public void postInit() {
    this.jvmName = JVMSharedJVMName.fromNormalName(name);
    fields = dataFields.keys().mut();
    for (Map.Entry<ROTuple, JVMMethodFieldType> f : methodFields) {
      fields.add((String) f.getKey().get(0));
    }
    staticFields = staticDataFields.keys().mut();
    for (Map.Entry<ROTuple, JVMMethodFieldType> f : staticMethodFields) {
      staticFields.add((String) f.getKey().get(0));
    }
  }

  public boolean resolveInternals(EvaluationContext context) {
    return false;
  }

  public boolean resolveInternals(EvaluationContext context, Location location) {
    boolean out = resolveInternals(context);
    if (!out) {
      context.moduleContext.errors.add(new ModuleError(location));
    }
    return out;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field0) {
    if (!resolveInternals(context, location)) return EvaluateResult.error;
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
    if (!resolveInternals(context, location)) return EvaluateResult.error;
    WholeValue key = WholeValue.getWhole(context, location, field0);
    if (!fields.contains((String) key.concreteValue())) {
      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new JVMPseudoField(lower, this, (String) key.concreteValue()));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.fromJVMName(jvmName);
  }
}
