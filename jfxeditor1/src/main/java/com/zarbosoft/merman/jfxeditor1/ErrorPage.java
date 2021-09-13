package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.displayderived.BoxContainer;
import com.zarbosoft.merman.editorcore.displayderived.PadContainer;
import com.zarbosoft.merman.editorcore.displayderived.TextBlock;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.merman.jfxeditor1.NotMain.META_KEY_ERROR;
import static com.zarbosoft.merman.jfxeditor1.NotMain.controlKeys;

public class ErrorPage {
  private BoxContainer displayRootBox;

  void clear(Editor editor) {
    if (displayRootBox != null) {
      editor.details.setInner(editor, null);
      displayRootBox = null;
    }
  }

  public void setAtom(Editor editor, Atom atom) {
    if (atom == null) {
      clear(editor);
      return;
    }
    TSList<String> errors = (TSList<String>) atom.meta.getOpt(META_KEY_ERROR);
    if (errors != null) {
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
    } else {
      clear(editor);
    }
  }

  public void destroy(Editor editor) {
    clear(editor);
  }
}
