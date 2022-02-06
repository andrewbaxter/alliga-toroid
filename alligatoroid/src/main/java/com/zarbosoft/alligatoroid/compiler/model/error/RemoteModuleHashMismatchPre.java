package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;

public class RemoteModuleHashMismatchPre extends Error.PreError {
  public final String url;
  public final String wantHash;
  public final String foundHash;

  public RemoteModuleHashMismatchPre(String url, String wantHash, String foundHash) {
    this.url = url;
    this.wantHash = wantHash;
    this.foundHash = foundHash;
  }

  @Override
  public String toString() {
    return Format.format(
        "Downloaded module at %s has hash %s but expected hash %s", url, foundHash, wantHash);
  }
}
