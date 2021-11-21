package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;

/** All accesses must occur within moduleLock */
public class ImportPath {
  public final ImportSpec spec;
  public final TSList<ImportPath> from = new TSList<>();

  public ImportPath(ImportSpec spec) {
    this.spec = spec;
  }

  public TSList<ImportSpec> findBefore(TSSet<ImportPath> seen, ImportSpec spec) {
    for (ImportPath fromSpec : from) {
      TSList<ImportSpec> found = fromSpec.find(seen, spec);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  public TSList<ImportSpec> find(TSSet<ImportPath> seen, ImportSpec spec) {
    if (seen.contains(this)) return null;
    seen.add(this);
    if (this.spec.equals(spec)) return new TSList<>(this.spec);
    TSList<ImportSpec> found = findBefore(seen, spec);
    if (found != null) {
      found.add(this.spec);
      return found;
    }
    return null;
  }
}
