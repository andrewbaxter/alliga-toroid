package com.zarbosoft.merman.standalone.display;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.display.Drawing;
import com.zarbosoft.merman.editor.display.DrawingContext;
import com.zarbosoft.merman.editor.visual.Vector;
import com.zarbosoft.merman.syntax.style.ModelColor;

public class MockeryDrawing extends MockeryDisplayNode implements Drawing {
	private Vector size = new Vector(0, 0);

	@Override
	public int converseSpan() {
		return size.converse;
	}

	@Override
	public void clear() {

	}

	@Override
	public void resize(final Context context, final Vector vector) {
		MockeryDrawing.this.size = vector;
	}

	@Override
	public DrawingContext begin(final Context context) {
		return new DrawingContext() {
			@Override
			public void setLineColor(final ModelColor color) {

			}

			@Override
			public void setLineCapRound() {

			}

			@Override
			public void setLineThickness(final double lineThickness) {

			}

			@Override
			public void setLineCapFlat() {

			}

			@Override
			public void setFillColor(final ModelColor color) {

			}

			@Override
			public void beginStrokePath() {

			}

			@Override
			public void beginFillPath() {

			}

			@Override
			public void moveTo(final int halfBuffer, final int halfBuffer1) {

			}

			@Override
			public void lineTo(final int i, final int i1) {

			}

			@Override
			public void closePath() {

			}

			@Override
			public void arcTo(final int c, final int t, final int c2, final int t2, final int radius) {

			}

			@Override
			public void translate(final int c, final int t) {

			}
		};
	}
}
