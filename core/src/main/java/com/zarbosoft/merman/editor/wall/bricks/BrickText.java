package com.zarbosoft.merman.editor.wall.bricks;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.display.DisplayNode;
import com.zarbosoft.merman.editor.display.Font;
import com.zarbosoft.merman.editor.display.Text;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.AlignmentListener;
import com.zarbosoft.merman.editor.visual.Vector;
import com.zarbosoft.merman.editor.wall.Brick;
import com.zarbosoft.merman.editor.wall.BrickInterface;
import com.zarbosoft.merman.syntax.style.Style;

public class BrickText extends Brick implements AlignmentListener {
	public Text text;

	public BrickText(final Context context, final BrickInterface inter) {
		super(inter);
		text = context.display.text();
		tagsChanged(context);
	}

	@Override
	public int converseEdge(final Context context) {
		return text.converseEdge(context);
	}

	public Properties properties(final Context context, final Style.Baked style) {
		final Font font = style.getFont(context);
		return new Properties(
				style.split,
				font.getAscent(),
				font.getDescent(),
				inter.getAlignment(style),
				font.getWidth(text.text())
		);
	}

	@Override
	public void tagsChanged(final Context context) {
		setStyle(context, context.getStyle(context.globalTags.plusAll(inter.getTags(context))));
	}

	public void setStyle(final Context context, final Style.Baked style) {
		this.style = style;
		if (text != null) {
			text.setColor(context, style.color);
			text.setFont(context, style.getFont(context));
		}
		final Alignment newAlignment = inter.getAlignment(style);
		if (alignment != newAlignment) {
			if (alignment != null)
				alignment.removeListener(context, this);
			if (newAlignment != null)
				newAlignment.addListener(context, this);
			alignment = newAlignment;
		}
		changed(context);
	}

	@Override
	public DisplayNode getDisplayNode() {
		return text;
	}

	@Override
	public void setConverse(final Context context, final int minConverse, final int converse) {
		this.minConverse = minConverse;
		text.setConverse(context, converse);
	}

	@Override
	public void allocateTransverse(final Context context, final int ascent, final int descent) {
		text.setTransverse(context, ascent);
	}

	public void setText(final Context context, final String text) {
		this.text.setText(context, text.replaceAll("\\p{Cntrl}", context.syntax.unprintable));
		changed(context);
	}

	@Override
	public int getConverse(final Context context) {
		return text.converse(context);
	}

	public Font getFont() {
		return text.font();
	}

	public int getConverseOffset(final int index) {
		return text.getConverseAtIndex(index);
	}

	public int getUnder(final Context context, final Vector point) {
		return text.getIndexAtConverse(context, point.converse);
	}
}
