package com.zarbosoft.merman.core.example;

import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.rendaw.common.ROSetRef;

public class SyntaxOut {
  public final ModelColor choiceCursorColor;
  public final ModelColor colorError;
  public final ModelColor colorInfo;
  public final double markTransverseOffset;
  public final Syntax syntax;
  public final ROSetRef<String> suffixOnPatternMismatch;
  public final Stylist stylist;

  public SyntaxOut(
      Stylist stylist,
      ModelColor colorChoiceCursor,
      ModelColor colorError,
      ModelColor colorInfo,
      Syntax syntax,
      ROSetRef<String> suffixOnPatternMismatch,
      double markTransverseOffset) {
    this.stylist = stylist;
    this.choiceCursorColor = colorChoiceCursor;
    this.colorError = colorError;
    this.colorInfo = colorInfo;
    this.syntax = syntax;
    this.suffixOnPatternMismatch = suffixOnPatternMismatch;
    this.markTransverseOffset = markTransverseOffset;
  }
}
