package com.zarbosoft.merman.jfxeditor1.modalinput;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.visual.visuals.VisualFrontPrimitive;
import com.zarbosoft.merman.editorcore.cursors.EditPrimitiveCursor;
import com.zarbosoft.merman.jfxeditor1.NotMain;

public class ModalPrimitiveCursor extends EditPrimitiveCursor {
  private final ModalPrimitiveInner inner;

  public ModalPrimitiveCursor(
      Context context,
      VisualFrontPrimitive visualPrimitive,
      boolean leadFirst,
      int beginOffset,
      int endOffset,
      NotMain main) {
    super(context, visualPrimitive, leadFirst, beginOffset, endOffset);
    inner = new ModalPrimitiveInner(context, main, this, getSyntaxPath());
  }

  @Override
  public boolean handleKey(Context context, ButtonEvent hidEvent) {
    if (inner.handleKey(context, hidEvent)) return true;
    return super.handleKey(context, hidEvent);
  }
}
