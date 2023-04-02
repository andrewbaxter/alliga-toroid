package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.derived.CourseGroup;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitive;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.function.Consumer;

public class AutocompleteChoice implements DetailsPageChooser.Choice {
  public final FieldPrimitive field;
  public final String text;

  public AutocompleteChoice(FieldPrimitive field, String text) {
    this.field = field;
    this.text = text;
  }

  public void choose(Editor editor, History.Recorder recorder) {
    Consumer<History.Recorder> apply =
        recorder1 -> {
          recorder1.apply(editor, new ChangePrimitive(field, 0, field.data.length(), text));
        };
    if (recorder != null) {
        apply.accept(recorder);
    } else {
        editor.history.record(editor, null, apply);
    }
  }

  @Override
  public ROList<CourseDisplayNode> display(Editor editor) {
    final Text text = editor.context.display.text();
    text.setBaselineTransverse(0);
    text.setText(editor.context, this.text);
    CourseGroup textPad = new CourseGroup(editor.context.display.group());
    editor.context.stylist.styleChoiceDescription(editor.context, text, textPad);
    textPad.add(text);
    return new TSList<CourseDisplayNode>(textPad);
  }
}
