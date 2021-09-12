package com.zarbosoft.merman.core.example;

import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.rendaw.common.ROSetRef;

public class SyntaxOut {
  public final ModelColor choiceCursor;
  public final double markTransverseOffset;
  public final Syntax syntax;
  public final ROSetRef<String> suffixOnPatternMismatch;
  public final Stylist stylist;

  public SyntaxOut(
      Stylist stylist,
      ModelColor choiceCursor,
      Syntax syntax,
      ROSetRef<String> suffixOnPatternMismatch,
      double markTransverseOffset) {
    this.stylist = stylist;
    this.choiceCursor = choiceCursor;
    this.syntax = syntax;
    this.suffixOnPatternMismatch = suffixOnPatternMismatch;
    this.markTransverseOffset = markTransverseOffset;
  }
}
