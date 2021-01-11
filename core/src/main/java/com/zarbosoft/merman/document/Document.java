package com.zarbosoft.merman.document;

import com.zarbosoft.merman.editor.serialization.Write;
import com.zarbosoft.merman.syntax.Syntax;

import java.io.OutputStream;
import java.nio.file.Path;

public class Document {
	final public Syntax syntax;
	final public Atom root;

	public Document(final Syntax syntax, final Atom root) {
		this.syntax = syntax;
		this.root = root;
	}

	public void write(final Path out) {
		Write.write(this, out);
	}

	public void write(final OutputStream stream) {
		Write.write(root, syntax, stream);
	}
}
