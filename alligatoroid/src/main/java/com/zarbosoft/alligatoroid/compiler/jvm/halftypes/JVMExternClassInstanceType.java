package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMPseudoFieldMeta;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMSoftType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin.nullType;

public class JVMExternClassInstanceType extends JVMClassInstanceType {
  @Param public TSList<SoftConstructor> softConstructors;
  @Param public TSMap<String, SoftField> softFields;
  @Param public TSMap<String, SoftField> softStaticFields;
  @Param public TSList<ImportId> softInherits;

  public static JVMExternClassInstanceType blank(JVMSharedNormalName name) {
    final JVMExternClassInstanceType out = new JVMExternClassInstanceType();
    out.name = name;
    out.constructors = new TSMap<>();
    out.fields = new TSMap<>();
    out.staticFields = new TSMap<>();
    out.inherits = new TSList<>();
    out.softConstructors = new TSList<>();
    out.softFields = new TSMap<>();
    out.softStaticFields = new TSMap<>();
    out.softInherits = new TSList<>();
    out.postInit();
    return out;
  }

  public static JVMType resolveType(EvaluationContext context, ImportId id) {
    final Value imported = Utils.await(context.moduleContext.getModule(id));
    if (imported == ErrorValue.error) return null;
    return (JVMType) ((ConstDataValue) imported).getInner();
  }

  private static boolean resolveFieldInner(
      EvaluationContext context, SoftField softField, JVMPseudoFieldMeta field) {
    for (SoftMethodField method : softField.methods) {
      JVMType outRaw = method.returnType.resolve(context);
      if (outRaw == null) {
        return false;
      }
      final TSList<JVMType> inRaw = new TSList<>();
      for (JVMSoftType argument : method.arguments) {
        JVMType type = argument.resolve(context);
        if (type == null) {
          return false;
        }
        inRaw.add(type);
      }
      field.methods.add(JVMUtils.methodSpecDetails(outRaw, inRaw, method.attributes));
    }
    if (softField.data != null) {
      JVMType type = softField.data.type.resolve(context);
      if (type == null) {
        return false;
      }
      field.data = new JVMUtils.DataSpecDetails(type, softField.data.attributes);
    }
    return true;
  }

  @Override
  protected boolean resolveParents(EvaluationContext context, Location location) {
    for (ImportId softInherit : softInherits) {
      final JVMType resolved = (JVMType) resolveType(context, softInherit);
      if (resolved == null) return false;
      inherits.add((JVMClassInstanceType) resolved);
    }
    softInherits.clear();
    return true;
  }

  @Override
  public boolean resolveConstructors(EvaluationContext context, Location location) {
    for (SoftConstructor softConstructor : softConstructors) {
      final TSList<JVMType> inRaw = new TSList<>();
      for (JVMSoftType argument : softConstructor.arguments) {
        final JVMType type = argument.resolve(context);
        if (type == null) return false;
        inRaw.add(type);
      }
      JVMUtils.MethodSpecDetails specDetails =
          JVMUtils.methodSpecDetails(nullType, inRaw, softConstructor.attributes);
      constructors.put(specDetails.argTuple, specDetails);
    }
    return true;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<String, JVMPseudoFieldMeta> field : fields) {
      out.add(field.getKey());
    }
    for (Map.Entry<String, SoftField> field : softFields) {
      out.add(field.getKey());
    }
    return out;
  }

  public ROList<String> traceStaticFields(EvaluationContext context, Location location) {
    final TSList<String> out = new TSList<>();
    for (Map.Entry<String, JVMPseudoFieldMeta> field : staticFields) {
      out.add(field.getKey());
    }
    for (Map.Entry<String, SoftField> field : softStaticFields) {
      out.add(field.getKey());
    }
    return out;
  }

  @Override
  public boolean resolveField(EvaluationContext context, Location location, String key) {
    if (fields.has(key)) return true;
    final SoftField softField = softFields.removeGetOpt(key);
    if (softField == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return false;
    }
    final JVMPseudoFieldMeta field = ensureField(key);
    return resolveFieldInner(context, softField, field);
  }

  @Override
  public boolean resolveStaticField(EvaluationContext context, Location location, String key) {
    if (staticFields.has(key)) return true;
    final SoftField softField = softStaticFields.removeGetOpt(key);
    if (softField == null) {
      context.moduleContext.errors.add(new NoField(location, key));
      return false;
    }
    final JVMPseudoFieldMeta field = ensureStaticField(key);
    return resolveFieldInner(context, softField, field);
  }

  public static class SoftConstructor implements AutoBuiltinExportable {
    @Param public TSList<JVMSoftType> arguments;
    @Param public JVMUtils.MethodSpecDetailsAttributes attributes;

    public static SoftConstructor create(
        TSList<JVMSoftType> arguments, JVMUtils.MethodSpecDetailsAttributes attributes) {
      final SoftConstructor out = new SoftConstructor();
      out.arguments = arguments;
      out.attributes = attributes;
      return out;
    }
  }

  public static class SoftMethodField implements AutoBuiltinExportable {
    @Param public JVMSoftType returnType;
    @Param public TSList<JVMSoftType> arguments;
    @Param public JVMUtils.MethodSpecDetailsAttributes attributes;

    public static SoftMethodField create(
        JVMSoftType returnType,
        TSList<JVMSoftType> arguments,
        JVMUtils.MethodSpecDetailsAttributes attributes) {
      final SoftMethodField out = new SoftMethodField();
      out.returnType = returnType;
      out.arguments = arguments;
      out.attributes = attributes;
      return out;
    }
  }

  public static class SoftDataField implements AutoBuiltinExportable {
    @Param public JVMSoftType type;
    @Param public JVMUtils.DataSpecDetailsAttributes attributes;

    public static SoftDataField create(
        JVMSoftType type, JVMUtils.DataSpecDetailsAttributes attributes) {
      final SoftDataField out = new SoftDataField();
      out.type = type;
      out.attributes = attributes;
      return out;
    }
  }

  public static class SoftField implements AutoBuiltinExportable {
    @Param public TSList<SoftMethodField> methods;
    @Param public SoftDataField data;

    public static SoftField blank() {
      final SoftField out = new SoftField();
      out.methods = new TSList<>();
      return out;
    }
  }
}
