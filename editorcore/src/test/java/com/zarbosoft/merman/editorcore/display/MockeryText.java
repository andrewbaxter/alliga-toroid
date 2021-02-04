package com.zarbosoft.merman.editorcore.display;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.display.Font;
import com.zarbosoft.merman.editor.display.Text;
import com.zarbosoft.merman.syntax.style.ModelColor;
import com.zarbosoft.rendaw.common.DeadCode;

public class MockeryText extends MockeryDisplayNode implements Text {
	String text;
	Font font = new MockeryFont(20);

	@Override
	public String text() {
		return text;
	}

	@Override
	public void setText(final Context context, final String text) {
		this.text = text;
	}

	@Override
	public void setColor(final Context context, final ModelColor color) {

	}

	@Override
	public Font font() {
		return font;
	}

	@Override
	public void setFont(final Context context, final Font font) {
		this.font = font;
	}

	@Override
	public int getIndexAtConverse(final Context context, final int converse) {
		throw new DeadCode();
	}

	@Override
	public int getConverseAtIndex(final int index) {
		if (index > text.length())
			throw new AssertionError();
		return font.getWidth(text.substring(0, index));
	}

	@Override
	public int converseSpan() {
		return font.getWidth(text);
	}

	@Override
	public int transverseSpan() {
		return font.getAscent() + font.getDescent();
	}
}
