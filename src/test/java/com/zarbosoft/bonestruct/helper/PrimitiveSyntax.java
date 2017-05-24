package com.zarbosoft.bonestruct.helper;

import com.zarbosoft.bonestruct.editor.visual.tags.FreeTag;
import com.zarbosoft.bonestruct.editor.visual.tags.StateTag;
import com.zarbosoft.bonestruct.syntax.FreeAtomType;
import com.zarbosoft.bonestruct.syntax.Syntax;

public class PrimitiveSyntax {
	public final static FreeAtomType primitive;
	public final static Syntax syntax;

	static {
		primitive = new Helper.TypeBuilder("primitive")
				.middlePrimitive("value")
				.back(Helper.buildBackDataPrimitive("value"))
				.frontDataPrimitive("value")
				.autoComplete(99)
				.build();
		syntax = new Helper.SyntaxBuilder("any")
				.type(primitive)
				.group("any", new Helper.GroupBuilder().type(primitive).build())
				.style(new StyleBuilder().tag(new StateTag("compact")).tag(new FreeTag("split")).broken(true).build())
				.build();
		syntax.rootFront.prefix.add(new Helper.FrontSpaceBuilder().tag("split").build());
	}
}
