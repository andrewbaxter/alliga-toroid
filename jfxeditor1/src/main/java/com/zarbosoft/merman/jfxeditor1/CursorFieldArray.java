package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldArray;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldArray;

import static com.zarbosoft.merman.jfxeditor1.NotMain.controlKeys;
import static com.zarbosoft.merman.jfxeditor1.NotMain.shiftKeys;

public class CursorFieldArray extends BaseEditCursorFieldArray {
  public final NotMain main;
  public ErrorPage errorPage;

  public CursorFieldArray(
      Context context,
      VisualFieldArray visual,
      boolean leadFirst,
      int start,
      int end,
      NotMain main) {
    super(context, visual, leadFirst, start, end);
    this.main = main;
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
    if (errorPage == null)
      // Because we can't initialize it before this is called via constructor
      errorPage = new ErrorPage();
    if (beginIndex == endIndex) errorPage.setAtom(editor, visual.value.data.get(beginIndex));
    else errorPage.setAtom(editor, null);
  }

  @Override
  public void destroy(Context context) {
    super.destroy(context);
    errorPage.destroy(Editor.get(context));
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
