package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import org.jetbrains.annotations.NotNull;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate.assertConstIntlike;

public class MortarTupleTypestate implements BuiltinAutoExportable, MortarDataTypestate {
  public static final JavaInternalName JVMNAME =
      JavaBytecodeUtils.internalNameFromClass(Tuple.class);
  public static final JavaDataDescriptor DESC = JavaDataDescriptor.fromJVMName(JVMNAME);
  public final TSList<MortarTupleFieldType> fields;

  public MortarTupleTypestate(TSList<MortarTupleFieldType> fields) {
    this.fields = fields;
  }

  @Override
  public ROList<String> typestate_traceFields(EvaluationContext context, Location location, Object inner) {
    final TSList<String> out = new TSList<>();
    for (int i = 0; i < fields.size(); i++) {
      out.add(Integer.toString(i));
    }
    return out;
  }

  @Override
  public EvaluateResult typestate_varCastTo(EvaluationContext context, Location location, MortarDataType prototype) {
  return code.consume();
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType prototype) {
    if (!(prototype instanceof MortarTupleType)) {
      return false;
    }
    final ROList<MortarDataType> otherFields = ((MortarTupleType) prototype).fields;
    if (fields.size() != otherFields.size()) {
      return false;
    }
    for (int i = 0; i < fields.size(); i++) {
      if (!fields.get(i).tuple_fieldtype_canCastTo(otherFields.get(i))) {
      return false;
      }
    }
    return true;
  }

  @Override
  public MortarDataType typestate_asType() {
    return newPrototype();
  }

  @NotNull
  private MortarTupleType newPrototype() {
    final TSList<MortarDataType> prototypes = new TSList<>();
    for (MortarTupleFieldType field : fields) {
      prototypes.add(field.tuple_fieldtype_newPrototype());
    }
    return new MortarTupleType(prototypes);
  }

  @Override
  public MortarProtofield typestate_newProtofield(MortarObjectInnerType parentMeta, String fieldName) {
  return new MortarDataProtofield(newPrototype(), parentMeta, fieldName);
  }

  @Override
  public JavaDataDescriptor typestate_jvmDesc() {
    return DESC;
  }

  public ROPair<Integer, MortarTupleFieldType> assertField(
      EvaluationContext context, Location location, Value field) {
    Integer key = assertConstIntlike(context, location, field);
    if (key == null) {
      return null;
    }
    if (key < 0 || key >= fields.size()) {
      context.errors.add(new NoField(location, key));
      return null;
    }
    return new ROPair<>(key, fields.get(key));
  }

  @Override
  public EvaluateResult typestate_varAccess(
          EvaluationContext context, Location location, Value field0, MortarDeferredCode baseCode) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuple_fieldtype_variableAsValue(context, location, base, field.first);
  }

  @Override
  public EvaluateResult typestate_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field0) {
    ROPair<Integer, MortarTupleFieldType> field = assertField(context, location, field0);
    if (field == null) {
      return EvaluateResult.error;
    }
    return field.second.tuple_fieldtype_constAsValue(context, location, value, field.first);
  }

  @Override
  public JavaBytecode typestate_arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadObj;
  }

  @Override
  public JavaBytecode typestate_arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreObj;
  }

  @Override
  public JavaBytecode typestate_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  @Override
  public JavaBytecode typestate_returnBytecode() {
    return JavaBytecodeUtils.returnObj;
  }

  @Override
  public JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(
        typestate_stackAsValue(((MortarTargetModuleContext) context.target).transfer((Exportable) data)));
  }
}
