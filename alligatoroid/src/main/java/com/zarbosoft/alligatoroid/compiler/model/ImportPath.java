package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;

public class ImportPath {
  public final ImportId spec;
  private final TSList<ImportPath> from = new TSList<>();

  public ImportPath(ImportId spec) {
    this.spec = spec;
  }

  public synchronized void add(ImportPath predecessor) {
    from.add(predecessor);
  }

  public synchronized TSList<ImportId> findBefore(TSSet<ImportPath> seen, ImportId spec) {
    for (ImportPath fromSpec : from) {
      TSList<ImportId> found = fromSpec.find(seen, spec);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  public synchronized TSList<ImportId> find(TSSet<ImportPath> seen, ImportId spec) {
    if (seen.contains(this)) {
        return null;
    }
    seen.add(this);
    if (this.spec.equals(spec)) {
        return new TSList<>(this.spec);
    }
    TSList<ImportId> found = findBefore(seen, spec);
    if (found != null) {
      found.add(this.spec);
      return found;
    }
    return null;
  }
}
