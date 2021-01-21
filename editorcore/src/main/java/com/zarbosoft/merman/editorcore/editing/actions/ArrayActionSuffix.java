package com.zarbosoft.merman.editorcore.editing.actions;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontArray;
import com.zarbosoft.merman.editorcore.editing.BaseGapAtomType;
import com.zarbosoft.merman.editorcore.editing.EditingExtension;
import com.zarbosoft.merman.editorcore.history.EditAction;
import com.zarbosoft.merman.editorcore.history.changes.ChangeArray;

import java.util.ArrayList;
import java.util.List;

import static com.zarbosoft.rendaw.common.Common.sublist;

@Action.StaticID(id = "suffix")
public class ArrayActionSuffix extends EditAction {
  private final VisualFrontArray.ArrayCursor cursor;

  public ArrayActionSuffix(EditingExtension edit, VisualFrontArray.ArrayCursor cursor) {
    super(edit);
    this.cursor = cursor;
  }

  @Override
  public boolean run1(final Context context) {
    final Atom gap = edit.suffixGap.create();
    List<Atom> transplant =
        new ArrayList<>(sublist(cursor.self.value.data, cursor.beginIndex, cursor.endIndex));
    edit.history.apply(
        context,
        new ChangeArray(
            cursor.self.value,
            cursor.beginIndex,
            cursor.endIndex - cursor.beginIndex,
            ImmutableList.of(gap)));
    edit.history.apply(
        context,
        new ChangeArray(
            (ValueArray) gap.fields.get(BaseGapAtomType.GAP_PRIMITIVE_KEY), 0, 0, transplant));
    gap.fields.getOpt("gap").selectInto(context);
    return true;
  }
}
