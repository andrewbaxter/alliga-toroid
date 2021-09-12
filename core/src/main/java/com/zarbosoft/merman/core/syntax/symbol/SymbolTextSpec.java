package com.zarbosoft.merman.core.syntax.symbol;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;
import com.zarbosoft.merman.core.wall.bricks.BrickText;
import com.zarbosoft.rendaw.common.ROMap;

public class SymbolTextSpec extends Symbol {
  public final String text;
  public final SplitMode splitMode;
  public final String alignmentId;
  public final String splitAlignmentId;
  private final ROMap<String, Object> meta;

  public SymbolTextSpec(Config config) {
    this.text = config.text;
    this.splitMode = config.splitMode;
    this.alignmentId = config.alignmentId;
    this.splitAlignmentId = config.splitAlignmentId;
    meta = config.meta;
  }

  @Override
  public CourseDisplayNode createDisplay(final Context context) {
    final Text text = context.display.text();
    text.setText(context, this.text);
    context.stylist.styleTextDisplay(context, text, meta);
    return text;
  }

  @Override
  public Brick createBrick(final Context context, final BrickInterface inter) {
    final BrickText out = new BrickText(context, inter, splitMode, alignmentId, splitAlignmentId);
    out.setText(context, this.text);
    context.stylist.styleText(context, out);
    return out;
  }

  @Override
  public void finish(MultiError errors, SyntaxPath typePath, AtomType atomType) {}

  @Override
  public ROMap<String, Object> meta() {
    return meta;
  }

  public static class Config {
    public final String text;
    public SplitMode splitMode = SplitMode.NEVER;
    public String alignmentId;
    public String splitAlignmentId;
    private ROMap<String, Object> meta = ROMap.empty;

    public Config(String text) {
      this.text = text;
    }

    public Config splitMode(SplitMode mode) {
      this.splitMode = mode;
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
