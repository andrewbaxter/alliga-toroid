package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtomBase;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.Refactorer;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorAtom;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.merman.jfxeditor1.NotMain.controlKeys;

public class CursorAtom extends BaseEditCursorAtom {
  public final NotMain main;
  private ErrorPage errorPage;
  private Refactorer refactorer;

  public CursorAtom(Context context, VisualAtom visual, int index, NotMain main) {
    super(context, visual, index);
    this.main = main;
    this.refactorer = new Refactorer();
  }

  @Override
  public void destroy(Context context) {
    super.destroy(context);
    final Editor editor = Editor.get(context);
    errorPage.destroy(editor);
    refactorer.close(editor);
  }

  @Override
  public void setIndex(Context context, int index) {
    super.setIndex(context, index);
    if (errorPage == null)
      // Because we can't initialize it before this is called via constructor
    {
        errorPage = new ErrorPage();
    }
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      errorPage.setAtom(Editor.get(context), ((VisualFieldAtomBase) selectable.second).atomGet());
    } else {
      errorPage.clearAtom(Editor.get(context));
    }
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    final Editor editor = Editor.get(context);
    if (NotMain.handleCommonNavigation(editor, main, hidEvent)) {
        return true;
    }
    if (hidEvent.press) {
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
              editCut(editor);
            } else {
              editDelete(editor);
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
            editPaste(editor);
            return true;
          }
        case S:
          {
            editSuffix(editor);
            return true;
          }
        case B:
          {
            editInsertBefore(editor);
            return true;
          }
        case A:
          {
            editInsertAfter(editor);
            return true;
          }
        case R:
          {
            ROPair<String, Visual> selectable = visual.selectable.get(index);
            if (selectable.second instanceof VisualFieldAtomBase) {
              refactorer.refactor(
                  editor, new TSList<>(((VisualFieldAtomBase) selectable.second).atomGet()));
            }
            return true;
          }
      }
    }
    return super.handleKey(context, hidEvent);
  }
}
