package com.zarbosoft.bonestruct.syntax.middle;

import com.zarbosoft.bonestruct.document.Node;
import com.zarbosoft.bonestruct.document.values.Value;
import com.zarbosoft.bonestruct.syntax.Syntax;
import com.zarbosoft.interface1.Configuration;

import java.util.Set;

@Configuration
public abstract class MiddleElement {
	public String id;

	public abstract void finish(Set<String> allTypes, Set<String> scalarTypes);

	public abstract Value create(Syntax syntax);

	public static abstract class Parent {
		public abstract Node node();
	}

	// TODO
	/*
	@Configuration(optional = true)
	boolean optional = false;
	 */
}