package com.zarbosoft.merman.helper;

import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontMarkBuilder {
  private final TSSet<String> tags = new TSSet<>();
  private final String text;
  private SplitMode splitMode;

  public FrontMarkBuilder(final String value) {
    text = value;
  }

  public FrontSymbolSpec build() {
    SymbolTextSpec.Config config = new SymbolTextSpec.Config(text);
    if (splitMode != null) config.splitMode(splitMode);
    return new FrontSymbolSpec(
        new FrontSymbolSpec.Config(new SymbolTextSpec(config)));
  }

  public FrontMarkBuilder tag(final String tag) {
    tags.add(tag);
    return this;
  }

  public FrontMarkBuilder split(SplitMode compact) {
    this.splitMode = compact;
    return this;
  }
}
