package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfBoolType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfIntType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfObjectType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfStringType;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.ModuleError;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeBool;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeInt;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Represents the metadata for interacting with a class - inheritance, fields */
public class JVMHalfClassType extends JVMHalfObjectType
    implements AutoBuiltinExportable, SimpleValue, LeafExportable {
  public static final String ACCESS_NEW = "new";
  public final TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors;
  public final TSMap<String, JVMHalfDataType> dataFields;
  public final TSMap<String, TSList<JVMMethodFieldType>> methodFields;
  public final TSMap<String, JVMHalfDataType> staticDataFields;
  public final TSMap<String, TSList<JVMMethodFieldType>> staticMethodFields;
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
      TSMap<String, TSList<JVMMethodFieldType>> methodFields,
      TSMap<String, JVMHalfDataType> staticDataFields,
      TSMap<String, TSList<JVMMethodFieldType>> staticMethodFields,
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

  public static ROTuple getArgTuple(MortarValue value) {
    if (value instanceof LooseTuple) {
      List data = new ArrayList();
      for (EvaluateResult e : ((LooseTuple) value).data) {
        data.add(getArgTupleInner(e.value));
      }
      return new ROTuple(data);
    } else return ROTuple.create(getArgTupleInner(value));
  }

  public boolean walkParents(Function<JVMHalfClassType, Boolean> process) {
    TSList<Iterator<JVMHalfClassType>> stack = new TSList<>();
    stack.add(Arrays.asList(this).iterator());
    while (stack.some()) {
      final Iterator<JVMHalfClassType> iterator = stack.last();
      JVMHalfClassType next = iterator.next();
      if (!iterator.hasNext()) stack.removeLast();
      final boolean res = process.apply(next);
      if (res) return true;
      final Iterator<JVMHalfClassType> parents = next.inherits.iterator();
      if (parents.hasNext()) stack.add(parents);
    }
    return false;
  }

  public JVMMethodFieldType findMethod(
      EvaluationContext context,
      Location location,
      TSMap<String, TSList<JVMMethodFieldType>> methods,
      String name,
      MortarValue argument) {
    if (!resolveInternals(context, location)) return null;
    ROTuple argTuple = getArgTuple(argument);
    ROList<JVMMethodFieldType> candidates = methods.getOpt(name);
    if (candidates == null) candidates = ROList.empty;
    for (JVMMethodFieldType candidate : candidates) {
      if (candidate.specDetails.argTuple.size() != argTuple.size()) continue;
      boolean allMatch = true;
      for (int i = 0; i < argTuple.size(); i += 1) {
        final JVMHalfDataType arg = (JVMHalfDataType) argTuple.get(i);
        final JVMHalfDataType bound = (JVMHalfDataType) candidate.specDetails.argTuple.get(i);
        if (arg instanceof JVMHalfClassType) {
          if (!walkParents(t -> t.jvmDesc().equals(bound.jvmDesc()))) {
            allMatch = false;
            break;
          }
        } else {
          if (!arg.jvmDesc().equals(bound.jvmDesc())) {
            allMatch = false;
            break;
          }
        }
      }
      if (!allMatch) continue;
      return candidate;
    }
    context.moduleContext.errors.add(JVMError.noMethodField(location, name));
    return null;
  }

  @Override
  public void postInit() {
    this.jvmName = JVMSharedJVMName.fromNormalName(name);
    fields = dataFields.keys().mut();
    for (Map.Entry<String, TSList<JVMMethodFieldType>> f : methodFields) {
      fields.add(f.getKey());
    }
    staticFields = staticDataFields.keys().mut();
    for (Map.Entry<String, TSList<JVMMethodFieldType>> f : staticMethodFields) {
      staticFields.add(f.getKey());
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
  public EvaluateResult mortarAccess(
      EvaluationContext context, Location location, MortarValue field0) {
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
      EvaluationContext context, Location location, MortarValue field0, JVMProtocode lower) {
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
