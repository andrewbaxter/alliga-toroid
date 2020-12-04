package com.zarbosoft.merman;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.document.values.ValueAtom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.history.changes.ChangeArray;
import com.zarbosoft.merman.editor.history.changes.ChangeNodeSet;
import com.zarbosoft.merman.editor.visual.visuals.VisualArray;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.MiscSyntax;
import com.zarbosoft.merman.helper.TreeBuilder;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.rendaw.common.Pair;
import org.junit.Test;

import static com.zarbosoft.merman.helper.Helper.assertTreeEqual;
import static com.zarbosoft.merman.helper.Helper.buildDoc;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/** Test changes to the selection when a change affects the selected nodes (or nearby nodes). */
public class TestSelectionChanges {

  @Test
  public void removeRootOnly() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.infinity).build(),
        new Path("value", "0"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray(Helper.rootArray(context.document), 0, 1, ImmutableList.of())),
        MiscSyntax.syntax.gap.create(),
        new Path("value", "0"));
  }

  private void innerTestTransform(
      final Syntax syntax,
      final Atom begin,
      Path selectBegin,
      final Pair.Consumer<Context, Atom> transform,
      final Atom end,
      final Path selectEnd) {
    final Context context = buildDoc(syntax, begin);

    // Initial selection and double checking
    final Atom found = (Atom) context.syntaxLocate(selectBegin);
    found.parent.selectUp(context);
    selectBegin = context.selection.getSyntaxPath();
    //assertThat(context.selection.getSyntaxPath(), equalTo(selectBegin));

    // Transform
    transform.accept(context, found);
    assertThat(Helper.rootArray(context.document).data.size(), equalTo(1));
    assertTreeEqual(Helper.rootArray(context.document).data.get(0), end);
    assertThat(context.selection.getSyntaxPath(), equalTo(selectEnd));

    // Undo
    context.history.undo(context);
    assertThat(Helper.rootArray(context.document).data.size(), equalTo(1));
    assertTreeEqual(Helper.rootArray(context.document).data.get(0), begin);
    assertThat(context.selection.getSyntaxPath(), equalTo(selectBegin));

    // Redo
    context.history.redo(context);
    assertThat(Helper.rootArray(context.document).data.size(), equalTo(1));
    assertTreeEqual(Helper.rootArray(context.document).data.get(0), end);
    assertThat(context.selection.getSyntaxPath(), equalTo(selectEnd));
  }

  @Test
  public void removeArrayOnly() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"),
        (context, selected) -> {
          context.history.apply(
              context,
              new ChangeArray((ValueArray) selected.parent.value(), 0, 1, ImmutableList.of()));
        },
        new TreeBuilder(MiscSyntax.array).addArray("value").build(),
        new Path("value", "0"));
  }

  @Test
  public void removeArraySelectBefore() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray((ValueArray) selected.parent.value(), 1, 1, ImmutableList.of())),
        new TreeBuilder(MiscSyntax.array)
            .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"));
  }

  @Test
  public void removeArraySelectFollowing() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray((ValueArray) selected.parent.value(), 0, 1, ImmutableList.of())),
        new TreeBuilder(MiscSyntax.array)
            .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"));
  }

  @Test
  public void removeArraySelectWithin() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray((ValueArray) selected.parent.value(), 1, 1, ImmutableList.of())),
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"));
  }

  @Test
  public void removeArraySelectWithinNoneAfter() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray((ValueArray) selected.parent.value(), 1, 1, ImmutableList.of())),
        new TreeBuilder(MiscSyntax.array)
            .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"));
  }

  @Test
  public void removeArraySelectDeep() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.array)
                    .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
                    .build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1", "value", "0"),
        (context, selected) -> {
          ((Value) context.syntaxLocate(new Path("value", "0", "value", "1", "value")))
              .parent
              .atom()
              .parent
              .delete(context);
        },
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"));
  }

  @Test
  public void addArrayAfter() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "0"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray(
                    (ValueArray) selected.parent.value(),
                    0,
                    0,
                    ImmutableList.of(new TreeBuilder(MiscSyntax.infinity).build()))),
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"));
  }

  @Test
  public void addArrayAfterEnd1() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "1"),
        (context, selected) ->
            context.history.apply(
                context,
                new ChangeArray(
                    (ValueArray) selected.parent.value(),
                    1,
                    0,
                    ImmutableList.of(new TreeBuilder(MiscSyntax.infinity).build()))),
        new TreeBuilder(MiscSyntax.array)
            .addArray(
                "value",
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build(),
                new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "2"));
  }

  @Test
  public void arrayBeginAtRemoveMultiple() {
    innerArrayTestTransform(
        0,
        0,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 0, 2, ImmutableList.of()));
        },
        0,
        0);
  }

  private void innerArrayTestTransform(
      final int beginSelectBegin,
      final int beginSelectEnd,
      final Pair.Consumer<Context, ValueArray> transform,
      final int endSelectBegin,
      final int endSelectEnd) {
    final Context context =
        buildDoc(
            MiscSyntax.syntax,
            new TreeBuilder(MiscSyntax.array)
                .addArray(
                    "value",
                    new TreeBuilder(MiscSyntax.one).build(),
                    new TreeBuilder(MiscSyntax.two).build(),
                    new TreeBuilder(MiscSyntax.three).build(),
                    new TreeBuilder(MiscSyntax.four).build(),
                    new TreeBuilder(MiscSyntax.five).build())
                .build());

    final ValueArray value =
        (ValueArray) Helper.rootArray(context.document).data.get(0).fields.getOpt("value");
    final VisualArray visual = (VisualArray) value.visual;
    visual.select(context, true, beginSelectBegin, beginSelectEnd);
    final VisualArray.ArraySelection selection = visual.selection;

    // Transform
    transform.accept(context, value);
    assertThat(selection.beginIndex, equalTo(endSelectBegin));
    assertThat(selection.endIndex, equalTo(endSelectEnd));

    // Undo
    context.history.undo(context);
    assertThat(selection.beginIndex, equalTo(beginSelectBegin));
    assertThat(selection.endIndex, equalTo(beginSelectEnd));

    // Redo
    context.history.redo(context);
    assertThat(selection.beginIndex, equalTo(endSelectBegin));
    assertThat(selection.endIndex, equalTo(endSelectEnd));
  }

  @Test
  public void arrayBeginRightBeforeRemoveMultiple() {
    innerArrayTestTransform(
        0,
        0,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 1, 2, ImmutableList.of()));
        },
        0,
        0);
  }

  @Test
  public void arrayBeginFarBeforeRemoveMultiple() {
    innerArrayTestTransform(
        0,
        0,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 2, 2, ImmutableList.of()));
        },
        0,
        0);
  }

  @Test
  public void arrayMidFarAfterRemoveMultiple() {
    innerArrayTestTransform(
        4,
        4,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 0, 2, ImmutableList.of()));
        },
        2,
        2);
  }

  @Test
  public void arrayMidRightAfterRemoveMultiple() {
    innerArrayTestTransform(
        3,
        3,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 0, 2, ImmutableList.of()));
        },
        1,
        1);
  }

  @Test
  public void arrayMidAtFirstRemoveMultiple() {
    innerArrayTestTransform(
        1,
        1,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 1, 2, ImmutableList.of()));
        },
        1,
        1);
  }

  @Test
  public void arrayMidAtSecondRemoveMultiple() {
    innerArrayTestTransform(
        2,
        2,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 1, 2, ImmutableList.of()));
        },
        1,
        1);
  }

  @Test
  public void arrayMidRightBeforeRemoveMultiple() {
    innerArrayTestTransform(
        1,
        1,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 2, 2, ImmutableList.of()));
        },
        1,
        1);
  }

  @Test
  public void arrayMidFarBeforeRemoveMultiple() {
    innerArrayTestTransform(
        1,
        1,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 3, 2, ImmutableList.of()));
        },
        1,
        1);
  }

  @Test
  public void arrayEndFarAfterRemoveMultiple() {
    innerArrayTestTransform(
        4,
        4,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 0, 2, ImmutableList.of()));
        },
        2,
        2);
  }

  @Test
  public void arrayEndRightAfterRemoveMultiple() {
    innerArrayTestTransform(
        4,
        4,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 3, 2, ImmutableList.of()));
        },
        2,
        2);
  }

  @Test
  public void arrayEndAtRemoveMultiple() {
    innerArrayTestTransform(
        3,
        3,
        (context, value) -> {
          context.history.apply(context, new ChangeArray(value, 3, 2, ImmutableList.of()));
        },
        2,
        2);
  }

  @Test
  public void arrayMidAtAddRemove() {
    innerArrayTestTransform(
        3,
        3,
        (context, value) -> {
          context.history.apply(
              context,
              new ChangeArray(
                  value,
                  3,
                  1,
                  ImmutableList.of(
                      new TreeBuilder(MiscSyntax.one).build(),
                      new TreeBuilder(MiscSyntax.one).build())));
        },
        4,
        4);
  }

  @Test
  public void arrayEndAtAddRemove() {
    innerArrayTestTransform(
        4,
        4,
        (context, value) -> {
          context.history.apply(
              context,
              new ChangeArray(
                  value,
                  4,
                  1,
                  ImmutableList.of(
                      new TreeBuilder(MiscSyntax.one).build(),
                      new TreeBuilder(MiscSyntax.one).build())));
        },
        5,
        5);
  }

  @Test
  public void removeNode() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.snooze)
            .add("value", new TreeBuilder(MiscSyntax.infinity).build())
            .build(),
        new Path("value", "0", "value", "atom"),
        (context, selected) -> {
          context.history.apply(
              context,
              new ChangeNodeSet(
                  (ValueAtom) selected.parent.value(), MiscSyntax.syntax.gap.create()));
        },
        new TreeBuilder(MiscSyntax.snooze).add("value", MiscSyntax.syntax.gap.create()).build(),
        new Path("value", "0", "value"));
  }

  @Test
  public void removeNodeSelectDeep() {
    innerTestTransform(
        MiscSyntax.syntax,
        new TreeBuilder(MiscSyntax.snooze)
            .add(
                "value",
                new TreeBuilder(MiscSyntax.array)
                    .addArray("value", new TreeBuilder(MiscSyntax.infinity).build())
                    .build())
            .build(),
        new Path("value", "0", "value", "atom","value","0"),
        (context, selected) -> {
          ((ValueArray) context.syntaxLocate(new Path("value", "0", "value","atom", "value")))
              .parent
              .atom()
              .parent
              .delete(context);
        },
        new TreeBuilder(MiscSyntax.snooze).add("value", MiscSyntax.syntax.gap.create()).build(),
        new Path("value", "0", "value"));
  }
}
