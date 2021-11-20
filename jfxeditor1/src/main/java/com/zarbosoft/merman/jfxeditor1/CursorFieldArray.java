package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldArray;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtomBase;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.Refactorer;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldArray;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.merman.jfxeditor1.NotMain.controlKeys;
import static com.zarbosoft.merman.jfxeditor1.NotMain.shiftKeys;

public class CursorFieldArray extends BaseEditCursorFieldArray {
  public final NotMain main;
  private Refactorer refactorer;

  public CursorFieldArray(
      Context context,
      VisualFieldArray visual,
      boolean leadFirst,
      int start,
      int end,
      NotMain main) {
    super(context, visual, leadFirst, start, end);
    this.main = main;
    updateErrorPage(Editor.get(context));
    refactorer = new Refactorer();
  }

  @Override
  protected void setBeginInternal(Context context, int index) {
    super.setBeginInternal(context, index);
    updateErrorPage(Editor.get(context));
  }

  @Override
  protected void setEndInternal(Context context, int index) {
    super.setEndInternal(context, index);
    updateErrorPage(Editor.get(context));
  }

  private void updateErrorPage(Editor editor) {
    if (main == null) return;
    if (beginIndex == endIndex) main.errorPage.setAtom(editor, visual.value.data.get(beginIndex));
    else main.errorPage.setAtom(editor, null);
  }

  @Override
  public void destroy(Context context) {
    super.destroy(context);
    final Editor editor = Editor.get(context);
    main.errorPage.setAtom(editor, null);
    refactorer.close(editor);
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    final Editor editor = Editor.get(context);
    if (NotMain.handleCommonNavigation(editor, main, hidEvent)) return true;
    if (hidEvent.press) {
      switch (hidEvent.key) {
        case DIR_SURFACE:
        case H:
          {
            actionExit(context);
            return true;
          }
        case DIR_NEXT:
        case J:
          {
            if (hidEvent.modifiers.containsAny(shiftKeys)) {
              if (leadFirst && beginIndex != endIndex) actionReleasePrevious(context);
              else actionGatherNext(context);
            } else {
              actionNextElement(context);
            }
            return true;
          }
        case DIR_PREV:
        case K:
          {
            if (hidEvent.modifiers.containsAny(shiftKeys)) {
              if (!leadFirst && beginIndex != endIndex) actionReleaseNext(context);
              else actionGatherPrevious(context);
            } else {
              actionPreviousElement(context);
            }
            return true;
          }
        case DIR_DIVE:
        case L:
          {
            actionEnter(context);
            return true;
          }
        case U:
          {
            actionLastElement(context);
            return true;
          }
        case I:
          {
            actionFirstElement(context);
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
          final TSList<Atom> selection = visual.value.data.sublist(beginIndex, endIndex);
            refactorer.refactor(editor, selection);
          return true;
        }
      }
    }
    return super.handleKey(context, hidEvent);
  }
}
