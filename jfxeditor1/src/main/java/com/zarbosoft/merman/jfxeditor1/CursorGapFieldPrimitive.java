package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.gap.BaseEditCursorGapFieldPrimitive;

public class CursorGapFieldPrimitive extends BaseEditCursorGapFieldPrimitive {
  public final NotMain main;
  private SyntaxPath syntaxPath;

  public CursorGapFieldPrimitive(
      Editor editor,
      VisualFieldPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset,
      NotMain main) {
    super(editor, visualPrimitive, leadFirst, beginOffset, endOffset);
    this.main = main;
    this.syntaxPath = syntaxPath;
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    return NotMain.handlePrimitiveNavigation(context, main, this, hidEvent);
  }
}
