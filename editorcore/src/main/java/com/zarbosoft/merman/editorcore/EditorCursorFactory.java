package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.visual.visuals.CursorAtom;
import com.zarbosoft.merman.core.visual.visuals.CursorFieldArray;
import com.zarbosoft.merman.core.visual.visuals.CursorFieldPrimitive;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldArray;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorAtom;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldArray;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldPrimitive2;
import com.zarbosoft.merman.editorcore.gap.BaseEditCursorGapFieldPrimitive;

public class EditorCursorFactory implements com.zarbosoft.merman.core.CursorFactory {
  public final Editor editor;

  public EditorCursorFactory(Editor editor) {
    this.editor = editor;
  }

  @Override
  public final CursorFieldPrimitive createFieldPrimitiveCursor(
      Context context,
      VisualFieldPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset) {
    Atom atom = visualPrimitive.value.atomParentRef.atom();
    if (atom.type == context.syntax.gap || atom.type == context.syntax.suffixGap)
      return createGapCursor(visualPrimitive, leadFirst, beginOffset, endOffset);
    else return createPrimitiveCursor1(context, visualPrimitive, leadFirst, beginOffset, endOffset);
  }

  public BaseEditCursorGapFieldPrimitive createGapCursor(
          VisualFieldPrimitive visualPrimitive, boolean leadFirst, int beginOffset, int endOffset) {
    return new BaseEditCursorGapFieldPrimitive(
        editor, visualPrimitive, leadFirst, beginOffset, endOffset);
  }

  public CursorFieldPrimitive createPrimitiveCursor1(
      Context context,
      VisualFieldPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset) {
    return new BaseEditCursorFieldPrimitive2(
        context, visualPrimitive, leadFirst, beginOffset, endOffset);
  }

  @Override
  public CursorFieldArray createFieldArrayCursor(
      Context context, VisualFieldArray visual, boolean leadFirst, int start, int end) {
    return new BaseEditCursorFieldArray(context, visual, leadFirst, start, end);
  }

  @Override
  public CursorAtom createAtomCursor(Context context, VisualAtom base, int index) {
    return new BaseEditCursorAtom(context, base, index);
  }

  @Override
  public boolean prepSelectEmptyArray(Context context, FieldArray value) {
    Editor editor = Editor.get(context);
    editor.history.record(
        editor,
        null,
        recorder -> {
          editor.arrayInsertNewDefault(recorder, value, 0);
        });
    return true;
  }
}
