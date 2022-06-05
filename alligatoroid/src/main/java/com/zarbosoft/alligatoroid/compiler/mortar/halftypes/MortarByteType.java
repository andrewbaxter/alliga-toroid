package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinSingletonExportable;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class MortarByteType implements MortarSimpleDataType, BuiltinSingletonExportable {
  public static final MortarByteType type = new MortarByteType();

  private MortarByteType() {}

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeValue(
      Object inner,
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    return SemiserialInt.create((Byte) inner);
  }

  @Override
  public Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data) {
    return data.dispatch(
        new SemiserialSubvalue.DefaultDispatcher<>() {
          @Override
          public Object handleInt(SemiserialInt s) {
            return (byte) s.value;
          }
        });
  }

  @Override
  public EvaluateResult valueVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(stackAsValue(JavaBytecodeUtils.literalIntShortByte((Byte) data)));
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BYTE;
  }

  @Override
  public JavaBytecode returnBytecode() {
    return JavaBytecodeUtils.returnIntShortByteBool;
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeIntShortByteBool(key);
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadIntShortByteBool(key);
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreByteBool;
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadByteBool;
  }
}
