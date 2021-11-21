package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public interface RefactorMatch extends DetailsPageChooser.Choice {
  void apply(Editor editor);

  String text();

  @Override
  default ROList<CourseDisplayNode> display(Editor editor) {
    final Text text = editor.context.display.text();
    editor.context.stylist.styleChoiceDescription(editor.context, text, null);
    text.setText(editor.context, text());
    return new TSList<>(text);
  }
}
