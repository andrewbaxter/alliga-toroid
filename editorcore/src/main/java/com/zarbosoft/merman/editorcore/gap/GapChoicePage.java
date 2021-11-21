package com.zarbosoft.merman.editorcore.gap;

import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.editorcore.Details;
import com.zarbosoft.merman.editorcore.DetailsPageChooser;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.rendaw.common.ROList;

public class GapChoicePage implements Details.Page {
  public final DetailsPageChooser<GapChoice> chooser;

  private GapChoicePage(Editor editor, ROList<GapChoice> choices) {
    chooser = new DetailsPageChooser<>(editor, choices);
  }

  public static GapChoicePage create(Editor editor, ROList<GapChoice> choices) {
    final GapChoicePage out = new GapChoicePage(editor, choices);
    editor.details.setTab(editor, GapChoicePage.class, out);
    return out;
  }

  @Override
  public void close(Editor editor) {
    closeInner(editor, GapChoicePage.class);
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
