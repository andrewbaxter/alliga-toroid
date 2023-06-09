package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.editorcore.displayderived.Box;
import com.zarbosoft.merman.editorcore.displayderived.ColumnarTableLayout;
import com.zarbosoft.merman.editorcore.displayderived.ConvScrollContainer;
import com.zarbosoft.merman.editorcore.displayderived.PadContainer;
import com.zarbosoft.merman.editorcore.displayderived.StackGroup;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class DetailsPageChooser<T extends DetailsPageChooser.Choice> {
  public final ROList<T> choices;
  private final Box highlight;
  private final Context.ContextDoubleListener edgeListener;
  private final ConvScrollContainer scroller = new ConvScrollContainer();
  public Container displayRoot;
  public int index = 0;
  TSList<ROList<CourseDisplayNode>> rows = TSList.of();

  public DetailsPageChooser(Editor editor, ROList<T> choices) {
    this.choices = choices;
    this.edgeListener =
        new Context.ContextDoubleListener() {
          @Override
          public void changed(Context context, double oldValue, double newValue) {
            updateScroll(context);
          }
        };
    editor.context.addConverseEdgeListener(edgeListener);
    final StackGroup group = new StackGroup(editor.context);

    highlight = new Box(editor.context);
    editor.context.stylist.styleObbox(editor.context, highlight, Stylist.ObboxType.CHOICE_CURSOR);
    group.add(highlight.drawing);

    ColumnarTableLayout table =
        new ColumnarTableLayout(editor.context, editor.detailSpan * editor.context.toPixels);
    table.setRowStride(editor.context, editor.choiceRowStride);
    table.setOuterColumnGap(editor.context, editor.choiceColumnSpace);
    table.setRowPadding(editor.context, editor.choiceRowPadding);
    group.addRoot(table);

    for (final Choice choice : choices) {
      ROList<CourseDisplayNode> display = choice.display(editor);
      rows.add(display);
      table.add(display);
    }
    table.layout();
    scroller.inner = group;
    changeChoice(editor.context, 0);
    this.displayRoot = new PadContainer(editor.context, editor.detailPad, scroller);
  }

  public void updateScroll(final Context context) {
    final ROList<CourseDisplayNode> row = rows.get(index);
    final DisplayNode preview = row.get(0);
    final DisplayNode text = row.last();
    final double converse = preview.converse();
    final double converseEdge = text.converseEdge();
    scroller.scrollVisible(converse, converseEdge, context.animateDetails);
  }

  private void changeChoice(final Context context, final int index) {
    this.index = index;
    final ROList<CourseDisplayNode> row = rows.get(index);
    final CourseDisplayNode preview = row.get(0);
    final CourseDisplayNode text = row.last();
    final double converse = preview.converse();
    final double transverse = Math.min(preview.transverse(), text.transverse());
    final double converseEdge = text.converseEdge();
    final double transverseEdge = Math.max(preview.transverseEdge(), text.transverseEdge());
    highlight.setSize(context, converseEdge - converse, transverseEdge - transverse);
    highlight.setPosition(new Vector(converse, transverse), false);
    updateScroll(context);
  }

  public void destroy(final Context context) {
    context.removeConverseEdgeListener(edgeListener);
  }

  public void nextChoice(Context context) {
    changeChoice(context, (index + 1) % choices.size());
  }

  public void previousChoice(Context context) {
    changeChoice(context, (index + choices.size() - 1) % choices.size());
  }

  public interface Choice {
    ROList<CourseDisplayNode> display(Editor editor);
  }
}
