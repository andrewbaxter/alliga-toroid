package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.editorcore.displayderived.PadContainer;
import com.zarbosoft.merman.editorcore.displayderived.TextBlock;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class Refactorer {
  public static Class tabKey = Refactorer.class;

  public void close(Editor editor) {
    editor.details.clearTab(editor, tabKey);
  }

  public void refactor(Editor editor, ROList<Atom> atom) {
    TSList<RefactorMatch> matches = new TSList<>();
    for (Refactor refactor : editor.refactors) {
      RefactorMatch match = refactor.check(atom);
      if (match != null) {
        matches.add(match);
      }
    }
    if (matches.some()) {
      RefactorsPage.create(editor, matches);
    } else {
      NoRefactorsPage.create(editor);
    }
  }

  public static class NoRefactorsPage implements Details.Page {
    private final PadContainer root;
    private Environment.HandleDelay delay;

    public NoRefactorsPage(Editor editor) {
      TextBlock text = new TextBlock(editor.context);
      editor.context.stylist.styleChoiceDescription(editor.context, text, null);
      text.setText(editor.context, "No refactors available.");
      root = new PadContainer(editor.context, editor.detailPad, text);
    }

    public static void create(Editor editor) {
      final NoRefactorsPage out = new NoRefactorsPage(editor);
      editor.details.setTab(editor, tabKey, out);
      out.delay =
          editor.context.env.delay(
              1000,
              () -> {
                out.closeInner(editor, tabKey);
              });
    }

    @Override
    public void close(Editor editor) {
      closeInner(editor, tabKey);
      if (delay != null) {
        delay.cancel();
        delay = null;
      }
    }

    @Override
    public Container inner() {
      return root;
    }

    @Override
    public boolean handleKey(Editor editor, ButtonEvent event) {
      if (event.press) {
        switch (event.key) {
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

  public static class RefactorsPage implements Details.Page {
    private final DetailsPageChooser<RefactorMatch> chooser;

    private RefactorsPage(Editor editor, ROList<RefactorMatch> choices) {
      chooser = new DetailsPageChooser<>(editor, choices);
    }

    public static void create(Editor editor, ROList<RefactorMatch> choices) {
      final RefactorsPage out = new RefactorsPage(editor, choices);
      editor.details.setTab(editor, tabKey, out);
    }

    @Override
    public void close(Editor editor) {
      closeInner(editor, tabKey);
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
              chooser.choices.get(chooser.index).apply(editor);
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
}
