package com.zarbosoft.merman.core.visual.visuals;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.CursorState;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.attachments.BorderAttachment;
import com.zarbosoft.merman.core.wall.Brick;

public class ArrayCursor extends com.zarbosoft.merman.core.Cursor {
  public final VisualFrontArray visual;
  public int beginIndex;
  public int endIndex;
  public boolean leadFirst;
  BorderAttachment border;

  public ArrayCursor(
      final Context context,
      final VisualFrontArray visual,
      final boolean leadFirst,
      final int start,
      final int end) {
    this.visual = visual;
    border = new BorderAttachment(context, context.syntax.cursorStyle.obbox);
    this.leadFirst = leadFirst;
    setRange(context, start, end);
  }

  public void setRange(final Context context, final int begin, final int end) {
    setBeginInternal(context, begin);
    setEndInternal(context, end);
    border.setFirst(context, visual.children.get(visual.visualIndex(begin)).getFirstBrick(context));
    border.setLast(context, visual.children.get(visual.visualIndex(end)).getLastBrick(context));
  }

  private void setBeginInternal(final Context context, final int index) {
    beginIndex = index;
    if (leadFirst) setCornerstone(context, beginIndex);
  }

  private void setCornerstone(final Context context, final int index) {
    context.wall.setCornerstone(
        context,
        visual
            .children
            .get(visual.visualIndex(index))
            .createOrGetCornerstoneCandidate(context)
            .brick,
        () -> {
          for (int at = visual.visualIndex(index) - 1; at >= 0; --at) {
            final Brick found = visual.children.get(at).getLastBrick(context);
            if (found != null) return found;
          }
          return visual.parent.getPreviousBrick(context);
        },
        () -> {
          for (int at = visual.visualIndex(index) + 1; at < visual.children.size(); ++at) {
            final Brick found = visual.children.get(at).getFirstBrick(context);
            if (found != null) return found;
          }
          return visual.parent.getNextBrick(context);
        });
  }

  private void setEndInternal(final Context context, final int index) {
    endIndex = index;
    if (!leadFirst) setCornerstone(context, endIndex);
  }

  public void setBegin(final Context context, final int index) {
    leadFirst = true;
    setBeginInternal(context, index);
    border.setFirst(context, visual.children.get(visual.visualIndex(index)).getFirstBrick(context));
  }

  public void setEnd(final Context context, final int index) {
    leadFirst = false;
    setEndInternal(context, index);
    border.setLast(context, visual.children.get(visual.visualIndex(index)).getLastBrick(context));
  }

  public void setPosition(final Context context, final int index) {
    setEndInternal(context, index);
    setBeginInternal(context, index);
    border.setFirst(context, visual.children.get(visual.visualIndex(index)).getFirstBrick(context));
    border.setLast(context, visual.children.get(visual.visualIndex(index)).getLastBrick(context));
  }

  @Override
  public void destroy(final Context context) {
    border.destroy(context);
    visual.selection = null;
  }

  @Override
  public Visual getVisual() {
    return visual.children.get(beginIndex);
  }

  @Override
  public CursorState saveState() {
    return new ArrayCursorState(visual.value, leadFirst, beginIndex, endIndex);
  }

  @Override
  public SyntaxPath getSyntaxPath() {
    return visual.value.getSyntaxPath().add(String.valueOf(beginIndex));
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handle(this);
  }

  public void actionEnter(final Context context) {
    visual.value.data.get(beginIndex).visual.selectAnyChild(context);
  }

  public void actionExit(final Context context) {
    visual.value.atomParentRef.selectAtomParent(context);
  }

  public void actionNext(final Context context) {
    visual.parent.selectNext(context);
  }

  public void actionPrevious(final Context context) {
    visual.parent.selectPrevious(context);
  }

  public void actionNextElement(final Context context) {
    ArrayCursor.this.leadFirst = true;
    final int newIndex = Math.min(visual.value.data.size() - 1, endIndex + 1);
    if (newIndex == beginIndex && newIndex == endIndex) return;
    setPosition(context, newIndex);
  }

  public void actionLastElement(final Context context) {
    final int newIndex = visual.value.data.size() - 1;
    if (newIndex == beginIndex && newIndex == endIndex) return;
    setPosition(context, newIndex);
  }

  public void actionFirstElement(final Context context) {
    final int newIndex = 0;
    if (newIndex == beginIndex && newIndex == endIndex) return;
    setPosition(context, newIndex);
  }

  public void actionPreviousElement(final Context context) {
    ArrayCursor.this.leadFirst = true;
    final int newIndex = Math.max(0, beginIndex - 1);
    if (newIndex == beginIndex && newIndex == endIndex) return;
    setPosition(context, newIndex);
  }

  public void actionCopy(final Context context) {
    context.copy(visual.value.data.sublist(beginIndex, endIndex + 1));
  }

  public void actionGatherNext(final Context context) {
    final int newIndex = Math.min(visual.value.data.size() - 1, endIndex + 1);
    if (endIndex == newIndex) return;
    setEnd(context, newIndex);
  }

  public void actionGatherLast(final Context context) {
    final int newIndex = visual.value.data.size() - 1;
    if (endIndex == newIndex) return;
    setEnd(context, newIndex);
  }

  public void actionGatherFirst(final Context context) {
    final int newIndex = 0;
    if (endIndex == newIndex) return;
    setBegin(context, newIndex);
  }

  public void actionReleaseAll(final Context context) {
    if (beginIndex == endIndex) return;
    if (leadFirst) setEnd(context, beginIndex);
    else setBegin(context, endIndex);
  }

  public void actionReleaseNext(final Context context) {
    final int newIndex = Math.max(beginIndex, endIndex - 1);
    if (endIndex == newIndex) return;
    setEnd(context, newIndex);
  }

  public void actionGatherPrevious(final Context context) {
    final int newIndex = Math.max(0, beginIndex - 1);
    if (beginIndex == newIndex) return;
    setBegin(context, newIndex);
  }

  public void actionReleasePrevious(final Context context) {
    final int newIndex = Math.min(endIndex, beginIndex + 1);
    if (beginIndex == newIndex) return;
    setBegin(context, newIndex);
  }

  public void actionWindow(final Context context) {
    final Atom root = visual.value.data.get(beginIndex);
    if (root.visual.selectAnyChild(context)) {
      context.windowExact(root);
      context.triggerIdleLayBricksOutward();
      return;
    }
  }
}
