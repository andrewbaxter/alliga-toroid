package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.editorcore.Details;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.displayderived.PadContainer;
import com.zarbosoft.merman.editorcore.displayderived.TextBlock;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.merman.jfxeditor1.NotMain.META_KEY_ERROR;

public class ErrorPage implements Atom.MetaListener {
  private Atom atom;
  private Page page;

  void clear(Editor editor) {
    if (page != null) {
      page.close(editor);
      page = null;
    }
  }

  public void metaChanged(Context context) {
    if (atom == null) {
        return;
    }
    Editor editor = Editor.get(context);
    clear(editor);
    TSList<Object> errors = (TSList<Object>) atom.metaGet(META_KEY_ERROR);
    if (errors == null || errors.isEmpty()) {
      return;
    }
    StringBuilder textText = new StringBuilder();
    for (Object error : errors) {
      textText.append(error + "\n");
    }
    page = new Page(editor, textText.toString());
  }

  public void clearAtom(Editor editor) {
    if (atom != null) {
      clear(editor);
      atom.removeMetaListener(this);
      atom = null;
    }
  }

  public void setAtom(Editor editor, Atom atom) {
    if (atom == this.atom) {
        return;
    }
    if (atom == null) {
      clearAtom(editor);
      return;
    }
    this.atom = atom;
    atom.addMetaListener(editor.context, this);
  }

  public void destroy(Editor editor) {
    clearAtom(editor);
  }

  private static class Page implements Details.Page {
    private PadContainer root;

    private Page(Editor editor, String textText) {
      TextBlock text = new TextBlock(editor.context);
      text.setText(editor.context, textText.toString());
      editor.context.stylist.styleChoiceDescription(editor.context, text, null);
      root = new PadContainer(editor.context, editor.detailPad, text);
      editor.details.setTab(editor, ErrorPage.class, this);
    }

    @Override
    public void close(Editor editor) {
      if (root != null) {
        closeInner(editor, ErrorPage.class);
        root = null;
      }
    }

    @Override
    public Container inner() {
      return root;
    }

    @Override
    public boolean handleKey(Editor editor, ButtonEvent event) {
      return false;
    }
  }
}
