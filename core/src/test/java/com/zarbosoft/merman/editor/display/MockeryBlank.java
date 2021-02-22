package com.zarbosoft.merman.editor.display;

import com.zarbosoft.merman.editor.Context;

public class MockeryBlank extends MockeryCourseDisplayNode implements Blank {
	private double converseSpan;

	@Override
	public double converseSpan() {
		return converseSpan;
	}

	@Override
	public double ascent() {
		return 0;
	}

	@Override
	public double descent() {
		return 0;
	}

	@Override
	public void setConverseSpan(final Context context, final double span) {
		this.converseSpan = span;
	}
}
