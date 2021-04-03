package com.zarbosoft.merman.core.syntax.symbol;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.BrickInterface;
import com.zarbosoft.merman.core.wall.bricks.BrickText;
import com.zarbosoft.merman.core.syntax.style.Style;

public class SymbolTextSpec extends Symbol {
  public final String text;
  public final Style.SplitMode splitMode;
  public final Style style;

  public SymbolTextSpec(Config config) {
    this.text = config.text;
    this.splitMode = config.splitMode;
    this.style = config.style;
  }

  @Override
  public DisplayNode createDisplay(final Context context) {
    final Text text = context.display.text();
    text.setText(context, this.text);
    text.setFont(context, Context.getFont(context, style));
    text.setColor(context, style.color);
    return text;
  }

  @Override
  public Brick createBrick(final Context context, final BrickInterface inter) {
    final BrickText out = new BrickText(context, inter, splitMode, style);
    out.setText(context, this.text);
    return out;
  }

  public static class Config {
    public final String text;
    public Style.SplitMode splitMode = Style.SplitMode.NEVER;
    public Style style = new Style.Config().create();

    public Config(String text) {
      this.text = text;
    }

    public Config splitMode(Style.SplitMode mode) {
      this.splitMode = mode;
      return this;
    }

    public Config style(Style style) {
      this.style = style;
      return this;
    }
  }
}
