package com.zarbosoft.merman.editorcore.helper;

import com.zarbosoft.merman.core.syntax.back.BackFixedArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.TSList;

public class BackArrayBuilder {
	TSList<BackSpec> elements = new TSList<>();

	public BackArrayBuilder add(final BackSpec part) {
		elements.add(part);
		return this;
	}

	public BackSpec build() {
		return new BackFixedArraySpec(new BackFixedArraySpec.Config().elements(elements));
	}
}
