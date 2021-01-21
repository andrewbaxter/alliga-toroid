package com.zarbosoft.merman.editorcore.editing.actions;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.visuals.VisualFrontAtomBase;
import com.zarbosoft.merman.editorcore.editing.EditingExtension;
import com.zarbosoft.merman.editorcore.history.EditAction;
import com.zarbosoft.merman.editorcore.history.changes.ChangeArray;

@Action.StaticID(id = "suffix")
public class AtomActionSuffix extends EditAction {
  private final VisualFrontAtomBase base;

  public AtomActionSuffix(EditingExtension edit, VisualFrontAtomBase base) {
    super(edit);
    this.base = base;
  }

  @Override
  public boolean run1(final Context context) {
    final Atom old = base.atomGet();
    final Atom gap = edit.suffixGap.create();
    edit.atomSet(context, edit.history, base, gap);
    edit.history.apply(
        context,
        new ChangeArray((ValueArray) gap.fields.getOpt("value"), 0, 0, ImmutableList.of(old)));
    gap.fields.getOpt("gap").selectInto(context);
    return true;
  }
}
