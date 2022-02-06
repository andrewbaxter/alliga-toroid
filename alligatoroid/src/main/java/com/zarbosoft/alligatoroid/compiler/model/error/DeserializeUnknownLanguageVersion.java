package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;

public class DeserializeUnknownLanguageVersion extends Error.DeserializeError {
  public DeserializeUnknownLanguageVersion(LuxemPath path, String version) {
    super(path, new PreError(version));
  }

  private static class PreError extends Error.PreError {
    public final String version;

    public PreError(String version) {
      this.version = version;
    }

    @Override
    public String toString() {
      return Format.format("Language version (luxem root type) %s is unknown", version);
    }
  }
}
