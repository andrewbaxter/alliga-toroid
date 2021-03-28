package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.editor.Path;
import com.zarbosoft.merman.core.syntax.AtomType;

public class AtomCandidatePluralBack extends BaseKVError{
  public AtomCandidatePluralBack(Path typePath, AtomType child, int childBackSize) {
      put("typePath", typePath);
      put("child", child);
      put("childBackSize", childBackSize);
  }

  @Override
  protected String description() {
    return "atom candidate serializes to multiple values in single value context";
  }
}
