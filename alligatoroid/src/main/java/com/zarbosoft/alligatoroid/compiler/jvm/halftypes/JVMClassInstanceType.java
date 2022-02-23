package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.errors.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMPseudoFieldMeta;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMPseudoFieldValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import static com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType.assertConstString;

public class JVMClassInstanceType implements AutoBuiltinExportable, JVMBaseObjectType {
  @Param public TSMap<ROTuple, JVMUtils.MethodSpecDetails> constructors;
  @Param public TSMap<String, JVMPseudoFieldMeta> fields;
  @Param public TSMap<String, JVMPseudoFieldMeta> staticFields;
  @Param public TSList<JVMClassInstanceType> inherits;
  @Param public JVMSharedNormalName name;
  @Param public JVMSharedJVMName jvmName;
  @Param public boolean error = false;

  public static JVMClassInstanceType blank(JVMSharedNormalName jvmExternalClass) {
    final JVMClassInstanceType out = new JVMClassInstanceType();
    out.name = jvmExternalClass;
    out.constructors = new TSMap<>();
    out.fields = new TSMap<>();
    out.staticFields = new TSMap<>();
    out.inherits = new TSList<>();
    out.postInit();
    return out;
  }

  public static boolean walkParents(
      EvaluationContext context,
      Location location,
      JVMClassInstanceType start,
      Function<JVMClassInstanceType, Boolean> process) {
    TSList<Iterator<JVMClassInstanceType>> stack = new TSList<>();
    stack.add(Arrays.asList(start).iterator());
    while (stack.some()) {
      final Iterator<JVMClassInstanceType> iterator = stack.last();
      JVMClassInstanceType next = iterator.next();
      if (!iterator.hasNext()) stack.removeLast();
      final boolean res = process.apply(next);
      if (res) return true;
      if (!next.resolveParents(context, location)) return false;
      final Iterator<JVMClassInstanceType> parents = next.inherits.iterator();
      if (parents.hasNext()) stack.add(parents);
    }
    return false;
  }

  public static JVMUtils.MethodSpecDetails findMethod(
      EvaluationContext context,
      Location location,
      TSList<JVMUtils.MethodSpecDetails> methods,
      String name,
      Value argument) {
    ROTuple argTuple = JVMTargetModuleContext.correspondJvmTypeTuple(argument);
    for (JVMUtils.MethodSpecDetails candidate : methods) {
      if (candidate.argTuple.size() != argTuple.size()) continue;
      boolean allMatch = true;
      for (int i = 0; i < argTuple.size(); i += 1) {
        final JVMType arg = (JVMType) argTuple.get(i);
        final JVMType bound = (JVMType) candidate.argTuple.get(i);
        if (arg instanceof JVMClassInstanceType) {
          if (!walkParents(
              context,
              location,
              (JVMClassInstanceType) arg,
              t -> t.jvmDesc().equals(bound.jvmDesc()))) {
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

  protected boolean resolveParents(EvaluationContext context, Location location) {
    return true;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<String, JVMPseudoFieldMeta> field : fields) {
      out.add(field.getKey());
    }
    return out;
  }

  public ROList<String> traceStaticFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<String, JVMPseudoFieldMeta> field : staticFields) {
      out.add(field.getKey());
    }
    return out;
  }

  public JVMPseudoFieldMeta ensureField(String name) {
    return fields.getCreate(name, () -> JVMPseudoFieldMeta.blank(this, name));
  }

  public JVMPseudoFieldMeta ensureStaticField(String name) {
    return staticFields.getCreate(name, () -> JVMPseudoFieldMeta.blank(this, name));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.fromJVMName(jvmName);
  }

  @Override
  public void postInit() {
    this.jvmName = JVMSharedJVMName.fromNormalName(name);
  }

  @Override
  public EvaluateResult valueAccess(
      EvaluationContext context, Location location, JVMProtocode carry, Value field0) {
    String key = assertConstString(context, location, field0);
    if (key == null) return EvaluateResult.error;
    if (!resolveField(context, location, key)) return EvaluateResult.error;
    final JVMPseudoFieldMeta field = fields.getOpt(key);
    if (field == null) {

      context.moduleContext.errors.add(new NoField(location, key));
      return EvaluateResult.error;
    }
    return EvaluateResult.pure(new JVMPseudoFieldValue(field, carry));
  }

  public boolean resolveStaticField(EvaluationContext context, Location location, String key) {
    boolean out = resolveStaticField(context, key);
    if (!out) {
      context.moduleContext.errors.add(Error.moduleError.toError(location));
    }
    return out;
  }

  private boolean resolveStaticField(EvaluationContext context, String key) {
    return true;
  }

  public boolean resolveField(EvaluationContext context, Location location, String key) {
    return true;
  }

  public boolean resolveConstructors(EvaluationContext context, Location location) {
    return true;
  }
}
