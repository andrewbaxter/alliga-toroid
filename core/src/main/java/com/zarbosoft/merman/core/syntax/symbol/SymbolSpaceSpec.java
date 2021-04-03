package com.zarbosoft.merman.core.syntax.symbol;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;
import com.zarbosoft.merman.core.wall.bricks.BrickEmpty;
import com.zarbosoft.merman.core.syntax.style.Style;

public class SymbolSpaceSpec extends Symbol {
  public final Style.SplitMode splitMode;
  public final Style style;

  public static class Config {
    public Style.SplitMode splitMode = Style.SplitMode.NEVER;
    public Style style = new Style.Config().create();

    public Config style(Style style) {
      this.style = style;
      return this;
    }

    public Config splitMode(Style.SplitMode splitMode) {
      this.splitMode = splitMode;
      return this;
    }
  }

  public SymbolSpaceSpec(Config config) {
    this.style = config.style;
    this.splitMode = config.splitMode;
  }

  @Override
  public DisplayNode createDisplay(final Context context) {
    final Blank blank = context.display.blank();
    blank.setConverseSpan(context, style.space);
    return blank;
  }

  @Override
  public Brick createBrick(final Context context, final BrickInterface inter) {
    return new BrickEmpty(context, inter, splitMode, style);
  }
}
