package com.zarbosoft.merman.editorcore.editing.actions;

import com.zarbosoft.merman.document.values.FieldPrimitive;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontPrimitive;
import com.zarbosoft.merman.editorcore.editing.EditingExtension;
import com.zarbosoft.merman.editorcore.history.EditAction;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitiveAdd;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitiveRemove;

public class PrimitiveActionPaste extends EditAction {
  private final VisualFrontPrimitive.PrimitiveCursor cursor;

  public PrimitiveActionPaste(EditingExtension edit, VisualFrontPrimitive.PrimitiveCursor cursor) {
    super(edit);
    this.cursor = cursor;
  }

  public String id() {
    return "paste";
  }

  @Override
  public void run1(final Context context) {
    context.uncopyString(
        text -> {
          if (text == null) return;
          FieldPrimitive value = cursor.visualPrimitive.value;
          if (cursor.range.beginOffset != cursor.range.endOffset)
            edit.history.apply(
                context,
                new ChangePrimitiveRemove(
                    value,
                    cursor.range.beginOffset,
                    cursor.range.endOffset - cursor.range.beginOffset));
          edit.history.apply(
              context, new ChangePrimitiveAdd(value, cursor.range.beginOffset, text));
        });
  }
}
