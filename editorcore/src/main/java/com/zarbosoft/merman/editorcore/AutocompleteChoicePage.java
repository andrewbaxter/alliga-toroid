package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.rendaw.common.ROList;

public class AutocompleteChoicePage implements Details.Page {
  public final DetailsPageChooser<AutocompleteChoice> chooser;

  private AutocompleteChoicePage(Editor editor, ROList<AutocompleteChoice> choices) {
    chooser = new DetailsPageChooser<>(editor, choices);
  }

  public static AutocompleteChoicePage create(Editor editor, ROList<AutocompleteChoice> choices) {
    final AutocompleteChoicePage out = new AutocompleteChoicePage(editor, choices);
    editor.details.setTab(editor, AutocompleteChoicePage.class, out);
    return out;
  }

  @Override
  public void close(Editor editor) {
    closeInner(editor, AutocompleteChoicePage.class);
  }

  @Override
  public Container inner() {
    return chooser.displayRoot;
  }

  @Override
  public boolean handleKey(Editor editor, ButtonEvent event) {
    if (event.press) {
      switch (event.key) {
        case DIR_PREV:
          {
            chooser.previousChoice(editor.context);
            return true;
          }
        case DIR_NEXT:
          {
            chooser.nextChoice(editor.context);
            return true;
          }
        case ENTER:
          {
            chooser.choices.get(chooser.index).choose(editor, null);
            close(editor);
            return true;
          }
        case ESCAPE:
          {
            close(editor);
            return true;
          }
      }
    }
    return false;
  }
}
