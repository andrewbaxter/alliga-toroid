package com.zarbosoft.merman.core.syntax.builder;

import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.syntax.alignments.AlignmentSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class TypeBuilder {
  private final String id;
  private final TSList<FrontSpec> front = new TSList<>();
  private final String humanName;
  private final TSMap<String, AlignmentSpec> alignments = new TSMap<>();
  private BackSpec back = null;
  private int depthScore = 1;
  private int precedence = Integer.MAX_VALUE;
  private boolean associateForward = false;
  private boolean autoChooseUnambiguous = true;

  public TypeBuilder(String id, String humanName) {
    this.id = id;
    this.humanName = humanName;
  }

  public TypeBuilder back(BackSpec spec) {
    if (back != null) {
        throw new Assertion();
    }
    back = spec;
    return this;
  }

  public TypeBuilder front(FrontSpec spec) {
    front.add(spec);
    return this;
  }

  public TypeBuilder precedence(int precedence) {
    this.precedence = precedence;
    return this;
  }

  public TypeBuilder associateForward() {
    this.associateForward = true;
    return this;
  }

  public TypeBuilder alignment(String key, AlignmentSpec spec) {
    alignments.putNew(key, spec);
    return this;
  }

  public FreeAtomType build() {
    return new FreeAtomType(
        new FreeAtomType.Config(humanName, new AtomType.Config(id, back, front.mut()))
            .depthScore(depthScore)
            .alignments(alignments)
            .precedence(precedence)
            .associateForward(associateForward)
            .autoChooseUnambiguous(autoChooseUnambiguous));
  }
}
