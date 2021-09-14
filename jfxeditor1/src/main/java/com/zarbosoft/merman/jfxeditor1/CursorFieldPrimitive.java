package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldPrimitive2;

import static com.zarbosoft.merman.jfxeditor1.NotMain.shiftKeys;

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
        hidEvent,
        new NotMain.PrimitiveKeyHandler() {
          @Override
          public boolean prev(ButtonEvent hidEvent) {
            if (hidEvent.modifiers.containsAny(shiftKeys)) {
              if (range.leadFirst) actionGatherPreviousLine(context);
              else actionReleaseNextLine(context);
            } else actionPreviousLine(context);
            return true;
          }

          @Override
          public boolean next(ButtonEvent hidEvent) {
            if (hidEvent.modifiers.containsAny(shiftKeys)) {
              if (range.leadFirst) actionReleasePreviousLine(context);
              else actionGatherNextLine(context);
            } else actionNextLine(context);
            return true;
          }

          @Override
          public boolean enter(ButtonEvent hidEvent) {
            editSplitLines(Editor.get(context));
            return true;
          }
        });
  }
}
