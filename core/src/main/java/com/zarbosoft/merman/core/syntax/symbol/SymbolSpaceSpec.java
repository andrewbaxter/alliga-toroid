package com.zarbosoft.merman.core.syntax.symbol;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;
import com.zarbosoft.merman.core.wall.bricks.BrickEmpty;
import com.zarbosoft.rendaw.common.ROMap;

public class SymbolSpaceSpec extends Symbol {
  public final SplitMode splitMode;
  public final String alignmentId;
  public final String splitAlignmentId;
  private final ROMap<String, Object> meta;

  public SymbolSpaceSpec(Config config) {
    this.splitMode = config.splitMode;
    this.alignmentId = config.alignmentId;
    this.splitAlignmentId = config.splitAlignmentId;
    meta = config.meta;
  }

  @Override
  public CourseDisplayNode createDisplay(final Context context) {
    final Blank blank = context.display.blank();
    context.stylist.styleEmptyDisplay(context, blank, meta);
    return blank;
  }

  @Override
  public Brick createBrick(final Context context, final BrickInterface inter) {
    BrickEmpty brick = new BrickEmpty(context, inter, splitMode, alignmentId, splitAlignmentId);
    context.stylist.styleEmpty(context, brick);
    return brick;
  }

  @Override
  public void finish(MultiError errors, SyntaxPath typePath, AtomType atomType) {}

  @Override
  public ROMap<String, Object> meta() {
    return meta;
  }

  public static class Config {
    public SplitMode splitMode = SplitMode.NEVER;
    public String alignmentId;
    public String splitAlignmentId;
    private ROMap<String, Object> meta = ROMap.empty;

    public Config splitMode(SplitMode splitMode) {
      this.splitMode = splitMode;
      return this;
    }

    public Config alignmentId(String alignmentId) {
      this.alignmentId = alignmentId;
      return this;
    }

    public Config splitAlignmentId(String splitAlignmentId) {
      this.splitAlignmentId = splitAlignmentId;
      return this;
    }

    public Config meta(ROMap<String, Object> meta) {
      this.meta = meta;
      return this;
    }
  }
}
