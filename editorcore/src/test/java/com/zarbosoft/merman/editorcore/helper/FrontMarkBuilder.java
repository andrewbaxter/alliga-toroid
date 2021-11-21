package com.zarbosoft.merman.editorcore.helper;

import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;

public class FrontMarkBuilder {
  private final String text;
  private SplitMode splitMode;

  public FrontMarkBuilder(final String value) {
    text = value;
  }

  public FrontSymbolSpec build() {
    SymbolTextSpec.Config config = new SymbolTextSpec.Config(text);
    if (splitMode != null) config.splitMode(splitMode);
    return new FrontSymbolSpec(new FrontSymbolSpec.Config(new SymbolTextSpec(config)));
  }

  public FrontMarkBuilder split(SplitMode compact) {
    this.splitMode = compact;
    return this;
  }
}
