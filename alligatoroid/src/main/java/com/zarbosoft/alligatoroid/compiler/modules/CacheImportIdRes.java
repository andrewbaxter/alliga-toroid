package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;

public class CacheImportIdRes {
  public final ImportId importId;
  public final long cacheId;

  public CacheImportIdRes(ImportId importId, long cacheId) {
    this.importId = importId;
    this.cacheId = cacheId;
  }
}
