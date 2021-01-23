package com.zarbosoft.merman.editorcore.editing.actions;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontArray;
import com.zarbosoft.merman.editorcore.editing.EditingExtension;
import com.zarbosoft.merman.editorcore.history.EditAction;

@Action.StaticID(id = "insert_after")
public class ArrayActionInsertAfter extends EditAction {
  private final VisualFrontArray.ArrayCursor cursor;

  public ArrayActionInsertAfter(EditingExtension edit, VisualFrontArray.ArrayCursor cursor) {
    super(edit);
    this.cursor = cursor;
  }

  @Override
  public boolean run1(final Context context) {
    final Atom created =
        edit.arrayInsertNewDefault(context, edit.history, cursor.self.value, cursor.endIndex + 1);
    if (!created.visual.selectAnyChild(context)) cursor.setPosition(context, cursor.endIndex + 1);
    return true;
  }
}
