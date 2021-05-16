package com.zarbosoft.merman.editorcore.cursors;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.syntax.BaseGapAtomType;
import com.zarbosoft.merman.core.syntax.GapAtomType;
import com.zarbosoft.merman.core.syntax.SuffixGapAtomType;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.visuals.CursorAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldAtomBase;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.history.changes.ChangeArray;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class EditCursorAtom extends CursorAtom {
  public EditCursorAtom(Context context, VisualAtom visual, int index) {
    super(context, visual, index);
  }

  public void editCut(Editor editor) {
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      actionCopy(editor.context);
      editor.history.record(
          editor.context,
          null,
          recorder -> {
            editor.atomSet(
                editor.context,
                recorder,
                (VisualFieldAtomBase) selectable.second,
                editor.createEmptyGap(editor.context.syntax.gap));
          });
    }
  }

  public void editDelete(Editor editor) {
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      editor.history.record(
          editor.context,
          null,
          recorder -> {
            editor.atomSet(
                editor.context,
                recorder,
                (VisualFieldAtomBase) selectable.second,
                editor.createEmptyGap(editor.context.syntax.gap));
          });
    }
  }

  public void editPaste(Editor editor) {
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      editor.context.uncopy(
          ((VisualFieldAtomBase) selectable.second).nodeType(),
          Context.UncopyContext.MAYBE_ARRAY,
          atoms -> {
            if (atoms.isEmpty()) return;
            editor.history.record(
                editor.context,
                null,
                recorder -> {
                  if (atoms.size() == 1) {
                    editor.atomSet(
                        editor.context,
                        recorder,
                        (VisualFieldAtomBase) selectable.second,
                        atoms.get(0));
                  } else {
                    Atom gap = editor.createEmptyGap(editor.context.syntax.suffixGap);
                    editor.atomSet(
                        editor.context, recorder, (VisualFieldAtomBase) selectable.second, gap);
                    recorder.apply(
                        editor.context,
                        new ChangeArray(
                            (FieldArray) gap.fields.get(BaseGapAtomType.PRIMITIVE_KEY),
                            0,
                            0,
                            atoms));
                  }
                });
          });
    }
  }

  public void editSuffix(Editor editor) {
    ROPair<String, Visual> selectable = visual.selectable.get(index);
    if (selectable.second instanceof VisualFieldAtomBase) {
      editor.history.record(
          editor.context,
          null,
          recorder -> {
            final Atom old = ((VisualFieldAtomBase) selectable.second).atomGet();
            final Atom gap = editor.createEmptyGap(editor.context.syntax.suffixGap);
            editor.atomSet(editor.context, recorder, (VisualFieldAtomBase) selectable.second, gap);
            recorder.apply(
                editor.context,
                new ChangeArray(
                    (FieldArray) gap.fields.get(SuffixGapAtomType.PRECEDING_KEY),
                    0,
                    0,
                    TSList.of(old)));
            gap.fields.get(GapAtomType.PRIMITIVE_KEY).selectInto(editor.context);
          });
    }
  }
}
