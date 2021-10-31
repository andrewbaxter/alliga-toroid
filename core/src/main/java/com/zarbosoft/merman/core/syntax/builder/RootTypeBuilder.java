package com.zarbosoft.merman.core.syntax.builder;

import com.zarbosoft.merman.core.syntax.RootAtomType;
import com.zarbosoft.merman.core.syntax.alignments.AlignmentSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class RootTypeBuilder {
  private BackSpec back;
  private final TSList<FrontSpec> front = new TSList<>();
  private final TSMap<String, AlignmentSpec> alignments = new TSMap<>();

  public RootTypeBuilder() {}

  public RootTypeBuilder back(BackSpec spec) {
    if (back != null) throw new Assertion();
    back = spec;
    return this;
  }

  public RootTypeBuilder front(FrontSpec spec) {
    front.add(spec);
    return this;
  }

  public RootTypeBuilder alignment(String key, AlignmentSpec spec) {
    alignments.putNew(key, spec);
    return this;
  }

  public RootAtomType build() {
    return new RootAtomType(
        new RootAtomType.Config(back, front.mut(), alignments.mut()));
  }
}
