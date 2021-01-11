package com.zarbosoft.merman.editorcore.editing.actions;

import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualPrimitive;
import com.zarbosoft.merman.editorcore.history.EditAction;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitiveAdd;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitiveRemove;

@Action.StaticID(id = "join")
public class PrimitiveActionJoinLines extends EditAction {
  private final VisualPrimitive.PrimitiveCursor cursor;

  public PrimitiveActionJoinLines(History history, VisualPrimitive.PrimitiveCursor cursor) {
    super(history);
    this.cursor = cursor;
  }

  @Override
  public boolean run1(final Context context) {
    VisualPrimitive.RangeAttachment range = cursor.range;
    int beginOffset = range.beginOffset;
    int endOffset = range.endOffset;
    VisualPrimitive.Line beginLine = range.beginLine;
    VisualPrimitive.Line endLine = range.endLine;
    if (beginOffset == endOffset) {
      if (beginLine.index + 1 >= cursor.visualPrimitive.lines.size()) return false;
      final int select = endLine.offset + endLine.text.length();
      history.apply(
          context,
          new ChangePrimitiveRemove(
              cursor.visualPrimitive.value,
              cursor.visualPrimitive.lines.get(beginLine.index + 1).offset - 1,
              1));
      cursor.visualPrimitive.select(context, true, select, select);
    } else {
      if (beginLine == endLine) return false;
      final StringBuilder replace = new StringBuilder();
      replace.append(beginLine.text.substring(beginOffset - beginLine.offset));
      final int selectBegin = beginOffset;
      int selectEnd = endOffset - 1;
      for (int index = beginLine.index + 1; index <= endLine.index - 1; ++index) {
        replace.append(cursor.visualPrimitive.lines.get(index).text);
        selectEnd -= 1;
      }
      replace.append(endLine.text.substring(0, endOffset - endLine.offset));
      history.apply(
          context,
          new ChangePrimitiveRemove(
              cursor.visualPrimitive.value, beginOffset, endOffset - beginOffset));
      history.apply(
          context,
          new ChangePrimitiveAdd(cursor.visualPrimitive.value, beginOffset, replace.toString()));
      cursor.visualPrimitive.select(context, true, selectBegin, selectEnd);
    }
    return true;
  }
}
