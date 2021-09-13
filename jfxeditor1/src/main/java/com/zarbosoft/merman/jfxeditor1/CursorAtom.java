package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtomBase;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorAtom;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.merman.jfxeditor1.NotMain.controlKeys;

public class CursorAtom extends BaseEditCursorAtom {
  public final NotMain main;
  private ErrorPage errorPage;

  public CursorAtom(Context context, VisualAtom visual, int index, NotMain main) {
    super(context, visual, index);
    this.main = main;
  }

  @Override
  public void destroy(Context context) {
    super.destroy(context);
    errorPage.destroy(Editor.get(context));
  }

  @Override
  public void setIndex(Context context, int index) {
    super.setIndex(context, index);
    if (errorPage == null)
      // Because we can't initialize it before this is called via constructor
      errorPage = new ErrorPage();
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      errorPage.setAtom(Editor.get(context), ((VisualFieldAtomBase) selectable.second).atomGet());
    }
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    if (NotMain.handleCommonNavigation(context, main, hidEvent)) return true;
    if (hidEvent.press)
      switch (hidEvent.key) {
        case DIR_SURFACE:
        case H:
          {
            actionExit(context);
            return true;
          }
        case DIR_DIVE:
        case L:
          {
            actionEnter(context);
            return true;
          }
        case DIR_NEXT:
        case J:
          {
            actionNextElement(context);
            return true;
          }
        case DIR_PREV:
        case K:
          {
            actionPreviousElement(context);
            return true;
          }
        case DELETE:
        case X:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              editCut(Editor.get(context));
            } else {
              editDelete(Editor.get(context));
            }
            return true;
          }
        case C:
          {
            actionCopy(context);
            return true;
          }
        case V:
          {
            editPaste(Editor.get(context));
            return true;
          }
        case S:
          {
            editSuffix(Editor.get(context));
            return true;
          }
        case B:
          {
            editInsertBefore(Editor.get(context));
            return true;
          }
        case A:
          {
            editInsertAfter(Editor.get(context));
            return true;
          }
      }
    return super.handleKey(context, hidEvent);
  }
}
