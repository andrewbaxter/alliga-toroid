package com.zarbosoft.merman;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.document.values.ValueAtom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.helper.BackArrayBuilder;
import com.zarbosoft.merman.helper.BackRecordBuilder;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.MiscSyntax;
import com.zarbosoft.merman.helper.SyntaxBuilder;
import com.zarbosoft.merman.helper.TreeBuilder;
import com.zarbosoft.merman.helper.TypeBuilder;
import com.zarbosoft.merman.syntax.Syntax;
import org.junit.Test;

import static com.zarbosoft.merman.helper.Helper.buildDoc;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestDocumentPaths {
  @Test
  public void testRoot() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.quoted).add("value", "").build(),
            new TreeBuilder(MiscSyntax.multiback).add("a", "").add("b", "").build(),
            new TreeBuilder(MiscSyntax.quoted).add("value", "").build());
    final Value value1 = Helper.rootArray(context.document).data.get(0).fields.get("value");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0", "value")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
    final Value value2 = Helper.rootArray(context.document).data.get(1).fields.get("b");
    assertThat(value2.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","1", "b")));
    assertThat(context.syntaxLocate(value2.getSyntaxPath()), equalTo(value2));
    final Value value3 = Helper.rootArray(context.document).data.get(2).fields.get("value");
    assertThat(value3.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","2", "value")));
    assertThat(context.syntaxLocate(value3.getSyntaxPath()), equalTo(value3));
  }

  @Test
  public void testRecord() {
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(
                new TypeBuilder("base")
                    .back(
                        new BackRecordBuilder()
                            .add("a", Helper.buildBackDataPrimitive("a"))
                            .build())
                    .frontDataPrimitive("a")
                    .build())
            .group("any", ImmutableList.of("base"))
            .build();
    final Context context =
        buildDoc(syntax, new TreeBuilder(syntax.types.get(0)).add("a", "").build());
    final Value value1 = Helper.rootArray(context.document).data.get(0).fields.get("a");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0", "a")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
  }

  @Test
  public void testArray() {
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(
                new TypeBuilder("base")
                    .back(new BackArrayBuilder().add(Helper.buildBackDataPrimitive("a")).build())
                    .frontDataPrimitive("a")
                    .build())
            .group("any", ImmutableList.of("base"))
            .build();
    final Context context =
        buildDoc(syntax, new TreeBuilder(syntax.types.get(0)).add("a", "").build());
    final Value value1 = Helper.rootArray(context.document).data.get(0).fields.get("a");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0", "a")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
  }

  @Test
  public void testDataNode() {
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(
                new TypeBuilder("base")
                    .back(Helper.buildBackDataAtom("a", "child"))
                    .frontDataNode("a")
                    .build())
            .type(
                new TypeBuilder("child")
                    .back(Helper.buildBackDataPrimitive("b"))
                    .frontDataPrimitive("b")
                    .build())
            .group("any", ImmutableList.of("base"))
            .build();
    final Context context =
        buildDoc(
            syntax,
            new TreeBuilder(syntax.types.get(0))
                .add("a", new TreeBuilder(syntax.types.get(1)).add("b", ""))
                .build());
    final Value value1 =
        ((ValueAtom) Helper.rootArray(context.document).data.get(0).fields.get("a"))
            .data.fields.get("b");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0","a","atom","b")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
  }

  @Test
  public void testDataArray() {
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(
                new TypeBuilder("base")
                    .back(Helper.buildBackDataArray("a", "child"))
                    .frontDataArray("a")
                    .build())
            .type(
                new TypeBuilder("child")
                    .back(Helper.buildBackDataPrimitive("b"))
                    .frontDataPrimitive("b")
                    .build())
            .group("any", ImmutableList.of("base"))
            .build();
    final Context context =
        buildDoc(
            syntax,
            new TreeBuilder(syntax.types.get(0))
                .addArray(
                    "a",
                    ImmutableList.of(new TreeBuilder(syntax.types.get(1)).add("b", "").build()))
                .build());
    final Value value1 =
        ((ValueArray) Helper.rootArray(context.document).data.get(0).fields.get("a"))
            .data
            .get(0)
            .fields
            .get("b");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0", "a","0","b")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
  }

  @Test
  public void testDataRecord() {
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(
                new TypeBuilder("base")
                    .back(Helper.buildBackDataRecord("a", "element"))
                    .frontDataArray("a")
                    .build())
            .type(
                new TypeBuilder("element")
                    .back(Helper.buildBackDataKey("k"))
                    .frontDataPrimitive("k")
                    .back(Helper.buildBackDataPrimitive("v"))
                    .frontDataPrimitive("v")
                    .build())
            .group("any", ImmutableList.of("base"))
            .build();
    final Context context =
        buildDoc(
            syntax,
            new TreeBuilder(syntax.types.get(0))
                .addRecord(
                    "a", new TreeBuilder(syntax.types.get(1)).add("k", "K").add("v", "V").build())
                .build());
    final Value value1 =
        ((ValueArray) Helper.rootArray(context.document).data.get(0).fields.get("a"))
            .data
            .get(0)
            .fields
            .get("v");
    assertThat(value1.getSyntaxPath().toList(), equalTo(ImmutableList.of("value","0", "a","0","v")));
    assertThat(context.syntaxLocate(value1.getSyntaxPath()), equalTo(value1));
  }

  @Test
  public void testLocateRootElement() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.quoted).add("value", "").build(),
            new TreeBuilder(MiscSyntax.multiback).add("a", "").add("b", "").build(),
            new TreeBuilder(MiscSyntax.quoted).add("value", "").build());
    assertThat(
        context.syntaxLocate(new Path("value","0")),
        equalTo(Helper.rootArray(context.document).data.get(0)));
    assertThat(
      context.syntaxLocate(new Path("value","0", "value")),
      equalTo(Helper.rootArray(context.document).data.get(0).fields.get("value")));
    assertThat(
        context.syntaxLocate(new Path("value","1", "a")),
        equalTo(Helper.rootArray(context.document).data.get(1).fields.get("a")));
    assertThat(
        context.syntaxLocate(new Path("value","1", "b")),
        equalTo(Helper.rootArray(context.document).data.get(1).fields.get("b")));
    assertThat(
        context.syntaxLocate(new Path("value","2", "value")),
        equalTo(Helper.rootArray(context.document).data.get(2).fields.get("value")));
  }

  @Test
  public void testLocateEmpty() {
    final Context context = buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.one).build());
    assertThat(
        context.syntaxLocate(new Path("value","0")),
        equalTo(Helper.rootArray(context.document).data.get(0)));
  }

  @Test
  public void testLocateArrayPrimitiveLong() {
    final Context context =
        buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "x").build());
    assertThat(
        context.syntaxLocate(new Path("value","0", "value")),
        equalTo(Helper.rootArray(context.document).data.get(0).fields.get("value")));
  }

  @Test
  public void testLocateArrayPrimitiveShort() {
    final Context context =
        buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "x").build());
    assertThat(
        context.syntaxLocate(new Path("value","0")),
        equalTo(Helper.rootArray(context.document).data.get(0)));
  }

  @Test
  public void testLocateNodePrimitiveLong() {
    final Atom quoted = new TreeBuilder(MiscSyntax.quoted).add("value", "x").build();
    final Context context =
        buildDoc(
            MiscSyntax.syntax, new TreeBuilder(MiscSyntax.snooze).add("value", quoted).build());
    assertThat(context.syntaxLocate(new Path("value","0", "value", "atom", "value")), equalTo(quoted.fields.get("value")));
  }

  @Test
  public void testLocateNodePrimitiveShort() {
    final Atom quoted = new TreeBuilder(MiscSyntax.quoted).add("value", "x").build();
    final Context context =
        buildDoc(
            MiscSyntax.syntax, new TreeBuilder(MiscSyntax.snooze).add("value", quoted).build());
    assertThat(context.syntaxLocate(new Path("value","0", "value")), equalTo(quoted.parent.value()));
  }

  @Test
  public void testLocatePrimitive() {
    final Context context =
        buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "").build());
    assertThat(
        context.syntaxLocate(new Path("value","0", "value")),
        equalTo(Helper.rootArray(context.document).data.get(0).fields.get("value")));
  }

  @Test
  public void testLocateRecordNode() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.plus)
                .add("first", new TreeBuilder(MiscSyntax.one))
                .add("second", new TreeBuilder(MiscSyntax.one))
                .build());
    assertThat(
        context.syntaxLocate(new Path("value","0")),
        equalTo(Helper.rootArray(context.document).data.get(0)));
    assertThat(
        context.syntaxLocate(new Path("value","0", "first", "atom")),
        equalTo(
            ((ValueAtom) Helper.rootArray(context.document).data.get(0).fields.get("first")).data));
    assertThat(
        context.syntaxLocate(new Path("value","0", "second", "atom")),
        equalTo(
            ((ValueAtom) Helper.rootArray(context.document).data.get(0).fields.get("second"))
                .data));
  }

  @Test
  public void testLocateRecordPrimitive() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.ratio).add("first", "").add("second", "").build());
    assertThat(
        context.syntaxLocate(new Path("value","0", "first")),
        equalTo(Helper.rootArray(context.document).data.get(0).fields.get("first")));
    assertThat(
        context.syntaxLocate(new Path("value","0", "second")),
        equalTo(Helper.rootArray(context.document).data.get(0).fields.get("second")));
  }

  @Test
  public void testLocateArrayElement() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.pair)
                .add("first", new TreeBuilder(MiscSyntax.one))
                .add("second", new TreeBuilder(MiscSyntax.one))
                .build());
    assertThat(
        context.syntaxLocate(new Path("value", "0", "first",  "atom")),
        equalTo(
            ((ValueAtom) Helper.rootArray(context.document).data.get(0).fields.get("first")).data));
    assertThat(
        context.syntaxLocate(new Path("value", "0", "second",  "atom")),
        equalTo(
            ((ValueAtom) Helper.rootArray(context.document).data.get(0).fields.get("second"))
                .data));
  }

  @Test
  public void testLocateDataRecordElement() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.record)
                .addRecord(
                    "value",
                    new TreeBuilder(MiscSyntax.recordElement)
                        .add("key", "first")
                        .add("value", new TreeBuilder(MiscSyntax.one))
                        .build(),
                    new TreeBuilder(MiscSyntax.recordElement)
                        .add("key", "second")
                        .add("value", new TreeBuilder(MiscSyntax.one))
                        .build())
                .build());
    assertThat(
        context.syntaxLocate(new Path("value","0", "value","0", "value","atom")),
        equalTo(
            ((ValueAtom)
                    ((ValueArray)
                            Helper.rootArray(context.document).data.get(0).fields.get("value"))
                        .data
                        .get(0)
                        .fields
                        .get("value"))
                .data));
    assertThat(
        context.syntaxLocate(new Path("value","0", "value","1", "value", "atom")),
        equalTo(
            ((ValueAtom)
                    ((ValueArray)
                            Helper.rootArray(context.document).data.get(0).fields.get("value"))
                        .data
                        .get(1)
                        .fields
                        .get("value"))
                .data));
  }

  @Test
  public void testLocateDataArrayElement() {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.array)
                .addArray(
                    "value",
                    new TreeBuilder(MiscSyntax.one).build(),
                    new TreeBuilder(MiscSyntax.one).build())
                .build());
    assertThat(
        context.syntaxLocate(new Path("value","0", "value","0")),
        equalTo(
            ((ValueArray) Helper.rootArray(context.document).data.get(0).fields.get("value"))
                .data.get(0)));
    assertThat(
        context.syntaxLocate(new Path("value","0", "value","1")),
        equalTo(
            ((ValueArray) Helper.rootArray(context.document).data.get(0).fields.get("value"))
                .data.get(1)));
  }
}
