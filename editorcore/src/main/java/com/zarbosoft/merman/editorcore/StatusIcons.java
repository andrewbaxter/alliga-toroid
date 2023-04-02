package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.FreeDisplayNode;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.visual.Vector;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;

public class StatusIcons {
  public final double size;
  public final double spacing;
  private final Group group;
  private final TSList<FreeDisplayNode> current = new TSList<>();
  private final TSSet<FreeDisplayNode> inGroup = new TSSet<>();
  private final double padConvStart;
  private final double padTransStart;

  public StatusIcons(Context context, double padConvStart, double padTransStart, double size, double spacing) {
    this.size = size;
    this.padConvStart = padConvStart;
    this.padTransStart = padTransStart;
    this.spacing = spacing;
    this.group = context.display.group();
    context.superBackground.add(group);
  }

  public void set(Context context, FreeDisplayNode icon) {
    double padding = this.spacing * context.toPixels;
    final double padConvStart = this.padConvStart * context.toPixels;
    final double padTransStart = this.padTransStart * context.toPixels;
    double size = this.size * context.toPixels;
    boolean slotted = false;
    for (int i = 0; i < current.size(); i++) {
      final FreeDisplayNode other = current.get(i);
      if (other == icon) {
          return;
      }
      if (other == null) {
        current.set(i, icon);
        icon.setPosition(new Vector(-size, padTransStart + i * (size + padding)), false);
        slotted = true;
        break;
      }
    }
    if (!slotted) {
      int i = current.size();
      current.add(icon);
      icon.setPosition(new Vector(-size, padTransStart + i * (size + padding)), false);
    }
    if (!inGroup.contains(icon)) {
      group.add(icon);
      inGroup.add(icon);
    }
    //icon.setConverse(padConvStart, true);
    icon.setConverse(padConvStart, false);
  }

  public void unset(Context context,FreeDisplayNode icon) {
    double size = this.size * context.toPixels;
    for (int i = 0; i < current.size(); i++) {
      final FreeDisplayNode other = current.get(i);
      if (other == icon) {
        current.set(i, null);
        //icon.setConverse(-size, true);
        icon.setConverse(-size, false);
        return;
      }
    }
  }
}
