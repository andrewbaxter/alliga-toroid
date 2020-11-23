package com.zarbosoft.merman.helper;

import com.zarbosoft.merman.syntax.front.FrontSymbol;
import com.zarbosoft.merman.syntax.symbol.SymbolTextSpec;

public class FrontMarkBuilder {
	private final FrontSymbol front;

	public FrontMarkBuilder(final String value) {
		this.front = new FrontSymbol();
		front.type = new SymbolTextSpec(value);
	}

	public FrontSymbol build() {
		return front;
	}

	public FrontMarkBuilder tag(final String tag) {
		front.tags.add(tag);
		return this;
	}
}
