package com.zarbosoft.merman.editorcore.helper;

import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.syntax.alignments.AlignmentSpec;
import com.zarbosoft.merman.core.syntax.alignments.ConcensusAlignmentSpec;
import com.zarbosoft.merman.core.syntax.alignments.RelativeAlignmentSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpec;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpecBase;
import com.zarbosoft.merman.core.syntax.front.FrontAtomSpec;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.syntax.symbol.SymbolSpaceSpec;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class TypeBuilder {
  private final String id;
  private final TSList<FrontSpec> front = new TSList<>();
  private final TSMap<String, AlignmentSpec> alignments = new TSMap<>();
  private BackSpec back;
  private boolean autoChooseUnambiguous = true;
  private int precedence = Integer.MAX_VALUE;
  private boolean associateForward = true;
  private int depthScore = 0;

  public TypeBuilder(final String id) {
    this.id = id;
  }

  public TypeBuilder back(final BackSpec back) {
    if (back != null) {
        throw new Assertion();
    }
    this.back = back;
    return this;
  }

  public FreeAtomType build() {
    FreeAtomType.Config config = new FreeAtomType.Config(id, new AtomType.Config(id, back, front));
    config.alignments = alignments;
    config.autoChooseUnambiguous = autoChooseUnambiguous;
    config.precedence = precedence;
    config.associateForward = associateForward;
    config.depthScore = depthScore;
    return new FreeAtomType(config);
  }

  public TypeBuilder front(final FrontSpec front) {
    this.front.add(front);
    return this;
  }

  public TypeBuilder autoComplete(boolean on) {
    autoChooseUnambiguous = on;
    return this;
  }

  public TypeBuilder frontDataAtom(final String middle) {
    this.front.add(new FrontAtomSpec(new FrontAtomSpec.Config(middle)));
    return this;
  }

  public TypeBuilder frontDataArray(final String middle) {
    this.front.add(
        new FrontArraySpec(new FrontArraySpec.Config(middle, new FrontArraySpecBase.Config())));
    return this;
  }

  public TypeBuilder frontDataPrimitive(final String middle) {
    this.front.add(new FrontPrimitiveSpec(new FrontPrimitiveSpec.Config(middle)));
    return this;
  }

  public TypeBuilder alignedFrontDataPrimitive(final String middle, String alignment) {
    this.front.add(
        new FrontPrimitiveSpec(new FrontPrimitiveSpec.Config(middle).firstAlignmentId(alignment)));
    return this;
  }

  public TypeBuilder frontSplitMark(final String value) {
    this.front.add(
        new FrontSymbolSpec(
            new FrontSymbolSpec.Config(
                new SymbolTextSpec(new SymbolTextSpec.Config(value).splitMode(SplitMode.ALWAYS)))));
    return this;
  }

  public TypeBuilder frontMark(final String value) {
    this.front.add(
        new FrontSymbolSpec(
            new FrontSymbolSpec.Config(new SymbolTextSpec(new SymbolTextSpec.Config(value)))));
    return this;
  }

  public TypeBuilder frontSpace(SplitMode splitMode) {
    this.front.add(
        new FrontSymbolSpec(
            new FrontSymbolSpec.Config(
                new SymbolSpaceSpec(new SymbolSpaceSpec.Config().splitMode(splitMode)))));
    return this;
  }

  public TypeBuilder precedence(final int precedence) {
    this.precedence = precedence;
    return this;
  }

  public TypeBuilder associateForward() {
    associateForward = true;
    return this;
  }

  public TypeBuilder associateBackward() {
    associateForward = false;
    return this;
  }

  public TypeBuilder depthScore(final int i) {
    depthScore = i;
    return this;
  }

  public TypeBuilder relativeAlignment(final String name, final String base, final int offset) {
    alignments.put(
        name, new RelativeAlignmentSpec(new RelativeAlignmentSpec.Config(base, offset, false)));
    return this;
  }

  public TypeBuilder concensusAlignment(final String name) {
    alignments.put(name, new ConcensusAlignmentSpec());
    return this;
  }
}
