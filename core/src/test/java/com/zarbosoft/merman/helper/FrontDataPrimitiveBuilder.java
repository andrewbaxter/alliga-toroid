package com.zarbosoft.merman.helper;

import com.zarbosoft.merman.core.example.DirectStylist;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.style.SplitMode;

public class FrontDataPrimitiveBuilder {
  private final String field;
  private SplitMode splitMode;
  private String compactAlignment;
  private String alignment;

  public FrontDataPrimitiveBuilder(final String field) {
    this.field = field;
  }

  public FrontPrimitiveSpec build() {
    FrontPrimitiveSpec.Config config = new FrontPrimitiveSpec.Config(field);
    if (splitMode != null) {
      config.splitMode(splitMode);
    }
    if (alignment != null) {
      config.firstAlignmentId(alignment);
    }
    if (compactAlignment != null) {
      config.splitAlignmentId(compactAlignment);
    }
    return new FrontPrimitiveSpec(config);
  }

  public FrontDataPrimitiveBuilder split(SplitMode splitMode) {
    this.splitMode = splitMode;
    return this;
  }

  public FrontDataPrimitiveBuilder alignment(String alignment) {
    this.alignment = alignment;
    return this;
  }

  public FrontDataPrimitiveBuilder compactAlignment(String compactAlignment) {
    this.compactAlignment = compactAlignment;
    return this;
  }
}
