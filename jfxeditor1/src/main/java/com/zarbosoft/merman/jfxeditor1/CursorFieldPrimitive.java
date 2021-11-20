package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldPrimitive2;

public class CursorFieldPrimitive extends BaseEditCursorFieldPrimitive2 {
  public final NotMain main;
  public ErrorPage errorPage;
  private SyntaxPath syntaxPath;

  public CursorFieldPrimitive(
      Context context,
      VisualFieldPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset,
      NotMain main) {
    super(context, visualPrimitive, leadFirst, beginOffset, endOffset);
    this.main = main;
    this.syntaxPath = syntaxPath;
    main.errorPage.setAtom(Editor.get(context), visualPrimitive.parent.atomVisual().atom);
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    return NotMain.handlePrimitiveNavigation(
        context,
        main,
        this,
        hidEvent
    );
  }
}
