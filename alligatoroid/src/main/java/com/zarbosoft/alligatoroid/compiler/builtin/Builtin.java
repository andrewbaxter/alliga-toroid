package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.CreatedFile;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue.nullValue;

/** Fields in top level builtin -- reflected into builtin value */
@Meta.BuiltinAggregate
public class Builtin {
  public static final Value _null = nullValue;
  public static final MortarNullType nullType = MortarNullType.type;
  public static final BuiltinJavaBytecode jbc = new BuiltinJavaBytecode();

  public static CreatedFile createFile(String path) {
    return new CreatedFile(path);
  }

  public static ImportId modRemote(String url, String hash) {
    return ImportId.create(RemoteModuleId.create(url, hash));
  }
}
