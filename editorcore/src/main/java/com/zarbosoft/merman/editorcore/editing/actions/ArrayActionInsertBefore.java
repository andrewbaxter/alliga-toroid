package com.zarbosoft.merman.editorcore.editing.actions;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontArray;
import com.zarbosoft.merman.editorcore.editing.EditingExtension;
import com.zarbosoft.merman.editorcore.history.EditAction;
import com.zarbosoft.merman.editorcore.history.History;

public class ArrayActionInsertBefore extends EditAction {
    public String id() {
        return "insert_before";
    }
  private final VisualFrontArray.ArrayCursor cursor;

  public ArrayActionInsertBefore(History history, VisualFrontArray.ArrayCursor cursor) {
    super(history);
    this.cursor = cursor;
  }

  @Override
  public boolean run1(final Context context) {
    final Atom created =
        EditingExtension.arrayInsertNewDefault(
            context, history, cursor.self.value, cursor.beginIndex);
    if (!created.visual.selectAnyChild(context)) cursor.setPosition(context, cursor.beginIndex);
    return true;
  }
}
