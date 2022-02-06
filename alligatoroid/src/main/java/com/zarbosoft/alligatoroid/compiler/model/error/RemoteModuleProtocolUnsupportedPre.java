package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;

public class RemoteModuleProtocolUnsupportedPre extends Error.PreError {
  public final String url;

  public RemoteModuleProtocolUnsupportedPre(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return Format.format("Remote module url [%s] has an unsupported protocol", url);
  }
}
