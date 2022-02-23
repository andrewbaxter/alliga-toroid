package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldId;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.editorcore.AutocompleteChoice;
import com.zarbosoft.merman.editorcore.AutocompleteChoicePage;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldPrimitive2;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class CursorFieldPrimitive extends BaseEditCursorFieldPrimitive2 {
  public final NotMain main;
  public ErrorPage errorPage;
  public AutocompleteChoicePage choicePage;
  private SyntaxPath syntaxPath;

  public CursorFieldPrimitive(
      Context context,
      VisualFieldPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset,
      NotMain main) {
    super(context, visualPrimitive, leadFirst, beginOffset, endOffset);
    this.main = main;
    this.syntaxPath = syntaxPath;
    main.errorPage.setAtom(Editor.get(context), visualPrimitive.parent.atomVisual().atom);
  }

  static double prefixSimilarity(String prefix, String whole) {
    int prefixI = 0;
    int wholeI = 0;
    int mismatchLength = 0;
    double error = 0;
    while (wholeI < whole.length()) {
      int endDist = whole.length() - wholeI;
      if (prefixI < prefix.length() && prefix.charAt(prefixI) == whole.charAt(wholeI)) {
        error -= Math.pow(((float) whole.length() / prefix.length()), 0.7);
        mismatchLength = 0;
        prefixI += 1;
      } else {
        mismatchLength += 1;
        error += ((endDist + 1f) + (float) mismatchLength) / whole.length();
      }
      wholeI += 1;
    }
    return error;
  }

  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    final String startText = visualPrimitive.value.data.toString();
    final boolean out = NotMain.handlePrimitiveNavigation(context, main, this, hidEvent);
    if (!visualPrimitive.value.data.toString().equals(startText)) {
      updateAutocomplete(Editor.get(context));
    }
    return out;
  }

  @Override
  public void editHandleTyping(Editor editor, History.Recorder recorder, String text) {
    super.editHandleTyping(editor, recorder, text);
    updateAutocomplete(editor);
  }

  @Override
  public void destroy(Context context) {
    if (choicePage != null) {
      Editor editor = Editor.get(context);
      choicePage.close(editor);
      choicePage = null;
    }
    super.destroy(context);
  }

  public void updateAutocomplete(Editor editor) {
    if (choicePage != null) {
      choicePage.close(editor);
      choicePage = null;
    }
    final FieldPrimitive field = visualPrimitive.value;
    final Atom atom = field.atomParentRef.atom();
    Integer id = null;
    for (Field unnamedField : atom.unnamedFields) {
      if (unnamedField instanceof FieldId) {
        id = ((FieldId) unnamedField).id;
        break;
      }
    }
    if (id == null) return;

    ROSetRef<String> options0 = editor.autocomplete.getOpt(id);
    if (options0 == null || options0.none()) return;
    final TSList<String> options = options0.toList();

    final String current = field.data.toString().toLowerCase(Locale.ROOT);
    ROPair<String, Double>[] sorted = new ROPair[options.size()];
    for (int i = 0; i < options.size(); i++) {
      final String option = options.get(i);
      sorted[i] = new ROPair<>(option, prefixSimilarity(option.toLowerCase(Locale.ROOT), current));
    }
    Arrays.sort(
        sorted,
        new Comparator<ROPair<String, Double>>() {
          @Override
          public int compare(ROPair<String, Double> o1, ROPair<String, Double> o2) {
            return Double.compare(o1.second, o2.second);
          }
        });

    TSList<AutocompleteChoice> choices = new TSList<>();
    for (ROPair<String, Double> option : sorted) {
      if (option.second > 5) break;
      choices.add(new AutocompleteChoice(field, option.first));
    }

    /// Update visual
    choicePage = AutocompleteChoicePage.create(editor, choices);
  }
}
