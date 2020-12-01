package com.zarbosoft.merman;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.Document;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.TreeBuilder;
import org.junit.Test;

import java.util.List;

import static com.zarbosoft.merman.helper.Helper.assertTreeEqual;
import static com.zarbosoft.merman.helper.SyntaxLoadSave.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestDocumentLoad {
	@Test
	public void primitive() {
		final Document doc = syntax.load("x");
		assertTreeEqual(Helper.rootArray(doc).data.get(0), new TreeBuilder(primitive).build());
		final List<Atom> top = Helper.rootArray(doc).data;
		assertThat(top.get(0).parent.path(), equalTo(new Path("value","0")));
	}

	@Test
	public void rootArray() {
		final Document doc = syntax.load("x,x");
		assertTreeEqual(Helper.rootArray(doc).data.get(0), new TreeBuilder(primitive).build());
		assertTreeEqual(Helper.rootArray(doc).data.get(1), new TreeBuilder(primitive).build());
		final List<Atom> top = Helper.rootArray(doc).data;
		assertThat(top.get(0).parent.path(), equalTo(new Path("value","0")));
		assertThat(top.get(1).parent.path(), equalTo(new Path("value","1")));
	}

	@Test
	public void record() {
		assertTreeEqual(Helper.rootArray(syntax.load("(typedRecord){a:x,b:y}")).data.get(0),
				new TreeBuilder(record).build()
		);
	}
}
