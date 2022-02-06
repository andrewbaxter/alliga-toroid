package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class DeserializeUnknownType extends Error.DeserializeError {
  public DeserializeUnknownType(LuxemPath path, String type, ROList<String> knownTypes) {
    super(path, new PreError(type, knownTypes));
  }

  private static class PreError extends Error.PreError {
    public final String type;
    public final ROList<String> knownTypes;

    public PreError(String type, ROList<String> knownTypes) {
      this.type = type;
      this.knownTypes = knownTypes;
    }

    @Override
    public String toString() {
      return Format.format("Unknown luxem type [%s]", type);
    }
  }
}
