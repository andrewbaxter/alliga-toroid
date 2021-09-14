package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.displayderived.BoxContainer;
import com.zarbosoft.merman.editorcore.displayderived.PadContainer;
import com.zarbosoft.merman.editorcore.displayderived.TextBlock;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.merman.jfxeditor1.NotMain.META_KEY_ERROR;

public class ErrorPage implements Atom.MetaListener {
  private Atom atom;
  private BoxContainer displayRootBox;

  void clear(Editor editor) {
    if (displayRootBox != null) {
      editor.details.removeInner(editor, displayRootBox);
      displayRootBox = null;
    }
  }

  public void metaChanged(Context context) {
    Editor editor = Editor.get(context);
    TSList<String> errors = (TSList<String>) atom.metaGet(META_KEY_ERROR);
    if (errors == null || errors.isEmpty()) {
      clear(editor);
      return;
    }
    TextBlock text = new TextBlock(editor.context);
    StringBuilder textText = new StringBuilder();
    for (String error : errors) {
      textText.append(error + "\n");
    }
    text.setText(editor.context, textText.toString());
    editor.context.stylist.styleChoiceDescription(editor.context, text, null);
    displayRootBox =
        new BoxContainer(
            editor.context, new PadContainer(editor.context, editor.detailPad).addRoot(text));
    editor.context.stylist.styleObbox(
        editor.context, displayRootBox, Stylist.ObboxType.DETAILS_BACKGROUND);
    editor.details.setInner(editor, displayRootBox);
  }

  public void clearAtom(Editor editor) {
    if (atom != null) {
      clear(editor);
      atom.removeMetaListener(this);
      atom = null;
    }
  }

  public void setAtom(Editor editor, Atom atom) {
    if (atom == this.atom) return;
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
}
