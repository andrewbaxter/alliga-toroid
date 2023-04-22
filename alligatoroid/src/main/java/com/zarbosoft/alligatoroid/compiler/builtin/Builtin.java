package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.CreatedFile;

/** Fields in top level builtin -- reflected into builtin value */
@StaticAutogen.BuiltinAggregate
public class Builtin {
  public final Value _null = Global.NULL_VALUE;
  public final NullType nullType = NullType.INST;
  public final BuiltinJavaBytecode jbc = new BuiltinJavaBytecode();

  public static CreatedFile createFile(String path) {
    return new CreatedFile(path);
  }

  public static ImportId modRemote(String url, String hash) {
    return ImportId.create(RemoteModuleId.create(url, hash));
  }
}
