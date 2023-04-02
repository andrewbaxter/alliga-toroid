package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.display.Container;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.merman.editorcore.displayderived.BeddingContainerRoot;
import com.zarbosoft.merman.editorcore.displayderived.Box;
import com.zarbosoft.merman.editorcore.displayderived.BoxContainer;
import com.zarbosoft.merman.editorcore.displayderived.VContainer;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class Details {
  private final double tabTransverseSpan;
  private final TSMap<Object, Slot> slotsLookup = new TSMap<>();
  private final TSList<Slot> slots = new TSList<>();
  private final Group boxGroup;
  private final TSList<Box> boxes = new TSList<>();
  private final BeddingContainerRoot detailsRoot;
  private final ObboxStyle tabStyle;
  private final VContainer vContainer;
  private final BoxContainer innerRoot;
  private Slot current;

  public Details(Editor editor, ObboxStyle tabStyle) {
    this.tabStyle = tabStyle;
    tabTransverseSpan = tabStyle.lineThickness * 4 * editor.context.toPixels;
    boxGroup = editor.context.display.group();
    vContainer = new VContainer(editor.context);
    vContainer.add(
        editor.context,
        new Container() {
          private Parent parent;

          @Override
          public void setConverseSpan(Context context, double span) {}

          @Override
          public Parent getParent() {
            return parent;
          }

          @Override
          public void setParent(Parent parent) {
            this.parent = parent;
          }

          @Override
          public void setPosition(Vector vector, boolean animate) {
            boxGroup.setPosition(vector, animate);
          }

          @Override
          public double converse() {
            return boxGroup.converse();
          }

          @Override
          public double transverse() {
            return boxGroup.transverse();
          }

          @Override
          public double transverseSpan() {
            return tabTransverseSpan;
          }

          @Override
          public double converseSpan() {
            return 0;
          }

          @Override
          public void setConverse(double converse, boolean animate) {
            boxGroup.setConverse(converse, animate);
          }

          @Override
          public Object inner_() {
            return boxGroup.inner_();
          }
        });
    innerRoot = new BoxContainer(editor.context);
    editor.context.stylist.styleObbox(
        editor.context, innerRoot, Stylist.ObboxType.DETAILS_BACKGROUND);
    innerRoot.set(editor.context, vContainer);
    detailsRoot = new BeddingContainerRoot(editor.context, false);
  }

  private void setCurrent(Editor editor, Slot slot) {
    current = slot;
    vContainer.add(editor.context, slot.page.inner());
  }

  public void setTab(Editor editor, Object key, Page page) {
    Slot slot = slotsLookup.getOpt(key);
    if (slot == null) {
      slot = new Slot();
      slotsLookup.put(key, slot);
      slots.add(slot);
      Box box = new Box(editor.context);
      box.setStyle(editor.context, tabStyle);
      final double tabConverseSpan = tabStyle.lineThickness * 20 * editor.context.toPixels;
      box.setSize(editor.context, tabConverseSpan, tabTransverseSpan);
      if (boxes.some()) {
        box.setConverse(
            boxes.last().converse()
                + tabConverseSpan
                + tabStyle.lineThickness * 3 * editor.context.toPixels);
      }
      boxes.add(box);
      boxGroup.add(box);
    }
    slot.page = page;
    if (current == null) {
      detailsRoot.setInner(editor, innerRoot);
    } else {
      current.page.inner().removeFromParent(editor.context);
    }
    setCurrent(editor, slot);
  }

  public boolean handleKey(Editor editor, ButtonEvent event) {
    if (current == null) {
        return false;
    }
    return current.page.handleKey(editor, event);
  }

  public interface Page {
    public void close(Editor editor);

    public Container inner();

    public boolean handleKey(Editor editor, ButtonEvent event);

    default void closeInner(Editor editor, Object key) {
      Slot slot = editor.details.slotsLookup.removeGetOpt(key);
      if (slot == null) {
        return;
      }
      if (this != null && slot.page != this) {
        return;
      }
      editor.details.slots.removeVal(slot);
      editor.details.boxGroup.removeAt(editor.details.boxes.size() - 1);
      editor.details.boxes.removeLast();
      if (slot == editor.details.current) {
        slot.page.inner().removeFromParent(editor.context);
        if (editor.details.slots.some()) {
          editor.details.setCurrent(editor, editor.details.slots.last());
        } else {
          editor.details.current = null;
          editor.details.detailsRoot.removeInner(editor, editor.details.innerRoot);
        }
      }
    }
  }

  public static class Slot {
    Page page;
  }
}
