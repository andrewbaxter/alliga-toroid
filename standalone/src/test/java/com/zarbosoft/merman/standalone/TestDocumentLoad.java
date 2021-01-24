package com.zarbosoft.merman.standalone;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.Document;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.standalone.helper.BackArrayBuilder;
import com.zarbosoft.merman.standalone.helper.BackRecordBuilder;
import com.zarbosoft.merman.standalone.helper.GroupBuilder;
import com.zarbosoft.merman.standalone.helper.Helper;
import com.zarbosoft.merman.standalone.helper.SyntaxBuilder;
import com.zarbosoft.merman.standalone.helper.TreeBuilder;
import com.zarbosoft.merman.standalone.helper.TypeBuilder;
import com.zarbosoft.merman.syntax.FreeAtomType;
import com.zarbosoft.merman.syntax.Syntax;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestDocumentLoad {
  /** Test that primitives can be deserialized. */
  @Test
  public void primitive() {
    final FreeAtomType primitive =
        new TypeBuilder("primitive").back(Helper.buildBackPrimitive("x")).frontMark("x").build();
    final Syntax syntax = new SyntaxBuilder("primitive").type(primitive).build();
    final Document doc = syntax.load("x");
    Helper.assertTreeEqual(Helper.rootArray(doc).data.get(0), new TreeBuilder(primitive).build());
    final List<Atom> top = Helper.rootArray(doc).data;
    Assert.assertThat(top.get(0).valueParentRef.path(), IsEqual.equalTo(new Path("value", "0")));
  }

  /** Test that multiple root elements can be deserialized. */
  @Test
  public void rootArray() {
    final FreeAtomType primitive =
        new TypeBuilder("primitive").back(Helper.buildBackPrimitive("x")).frontMark("x").build();
    final Syntax syntax =
        new SyntaxBuilder("array_value")
            .type(primitive)
            .group("array_value", new GroupBuilder().type(primitive).build())
            .build();
    final Document doc = syntax.load("x,x");
    Helper.assertTreeEqual(Helper.rootArray(doc).data.get(0), new TreeBuilder(primitive).build());
    Helper.assertTreeEqual(Helper.rootArray(doc).data.get(1), new TreeBuilder(primitive).build());
    final List<Atom> top = Helper.rootArray(doc).data;
    Assert.assertThat(top.get(0).valueParentRef.path(), IsEqual.equalTo(new Path("value", "0")));
    Assert.assertThat(top.get(1).valueParentRef.path(), IsEqual.equalTo(new Path("value", "1")));
  }

  @Test
  public void record() {
    final FreeAtomType primitive =
        new TypeBuilder("primitive").back(Helper.buildBackPrimitive("x")).frontMark("x").build();
    final FreeAtomType typedPrimitive =
        new TypeBuilder("typedPrimitive")
            .back(Helper.buildBackType("z", Helper.buildBackPrimitive("x")))
            .frontMark("x")
            .build();
    final FreeAtomType doublePrimitive =
        new TypeBuilder("doublePrimitive")
            .back(Helper.buildBackPrimitive("x"))
            .back(Helper.buildBackPrimitive("y"))
            .frontMark("x")
            .build();
    final FreeAtomType array =
        new TypeBuilder("array")
            .back(
                Helper.buildBackType(
                    "typedArray",
                    new BackArrayBuilder()
                        .add(Helper.buildBackPrimitive("x"))
                        .add(Helper.buildBackPrimitive("y"))
                        .build()))
            .frontMark("x")
            .build();
    final FreeAtomType record =
        new TypeBuilder("record")
            .back(
                Helper.buildBackType(
                    "typedRecord",
                    new BackRecordBuilder()
                        .add("a", Helper.buildBackPrimitive("x"))
                        .add("b", Helper.buildBackPrimitive("y"))
                        .build()))
            .frontMark("x")
            .build();
    final FreeAtomType dataPrimitive =
        new TypeBuilder("dataPrimitive")
            .back(Helper.buildBackDataPrimitive("value"))
            .frontDataPrimitive("value")
            .build();
    final FreeAtomType dataArray =
        new TypeBuilder("dataArray")
            .back(Helper.buildBackDataArray("value", "array_value"))
            .frontDataArray("value")
            .build();
    final FreeAtomType dataRecord =
        new TypeBuilder("dataRecord")
            .back(Helper.buildBackDataRecord("value", "dataRecordElement"))
            .frontDataArray("value")
            .build();
    final FreeAtomType dataRecordElement =
        new TypeBuilder("dataRecordElement")
            .back(Helper.buildBackDataKey("key"))
            .back(Helper.buildBackDataAtom("value", "value"))
            .frontDataPrimitive("key")
            .frontDataNode("value")
            .build();
    final Syntax syntax =
        new SyntaxBuilder("array_value")
            .type(primitive)
            .type(typedPrimitive)
            .type(doublePrimitive)
            .type(array)
            .type(record)
            .type(dataPrimitive)
            .type(dataArray)
            .type(dataRecord)
            .type(dataRecordElement)
            .group(
                "array_value",
                new GroupBuilder()
                    .type(primitive)
                    .type(typedPrimitive)
                    .type(doublePrimitive)
                    .type(array)
                    .type(record)
                    .type(dataPrimitive)
                    .type(dataArray)
                    .type(dataRecord)
                    .build())
            .group(
                "value",
                new GroupBuilder()
                    .type(primitive)
                    .type(typedPrimitive)
                    .type(array)
                    .type(record)
                    .type(dataPrimitive)
                    .type(dataArray)
                    .type(dataRecord)
                    .build())
            .build();
    Helper.assertTreeEqual(
        Helper.rootArray(syntax.load("(typedRecord){a:x,b:y}")).data.get(0),
        new TreeBuilder(record).build());
  }
}
