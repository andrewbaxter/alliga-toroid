package com.zarbosoft.merman;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.history.changes.ChangeArray;
import com.zarbosoft.merman.editor.visual.tags.TypeTag;
import com.zarbosoft.merman.helper.FrontMarkBuilder;
import com.zarbosoft.merman.helper.GeneralTestWizard;
import com.zarbosoft.merman.helper.GroupBuilder;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.MiscSyntax;
import com.zarbosoft.merman.helper.StyleBuilder;
import com.zarbosoft.merman.helper.SyntaxBuilder;
import com.zarbosoft.merman.helper.TreeBuilder;
import com.zarbosoft.merman.helper.TypeBuilder;
import com.zarbosoft.merman.syntax.FreeAtomType;
import com.zarbosoft.merman.syntax.Syntax;
import org.junit.Test;

public class TestLiveExamples {

  @Test
  public void testDeselectGapInArray() {
    new GeneralTestWizard(MiscSyntax.syntax, MiscSyntax.syntax.gap.create())
        .act("enter")
        .run(context -> context.cursor.receiveText(context, "["))
        .checkBrickCount(3)
        .run(context -> context.cursor.receiveText(context, "e"))
        .checkArrayTree(
            new TreeBuilder(MiscSyntax.array)
                .addArray("value", new TreeBuilder(MiscSyntax.syntax.gap).add("gap", "e").build())
                .build())
        .act("exit")
        .act("exit");
  }

  @Test
  public void testDeleteArray() {
    new GeneralTestWizard(MiscSyntax.syntax, MiscSyntax.syntax.gap.create())
        .act("enter")
        .run(context -> context.cursor.receiveText(context, "["))
        .act("exit")
        .act("exit")
        .checkArrayTree(new TreeBuilder(MiscSyntax.array).addArray("value").build())
        .act("delete")
        .checkBrickCount(1);
  }

  @Test
  public void testAddElementAfterArray() {
    new GeneralTestWizard(MiscSyntax.syntax, MiscSyntax.syntax.gap.create())
        .act("enter")
        .sendText("[")
        .checkTextBrick(0, 0, "[")
        .checkTextBrick(0, 2, "]")
        .act("exit")
        .checkTextBrick(0, 0, "[")
        .checkSpaceBrick(0, 1)
        .checkTextBrick(0, 2, "]")
        .act("insert_after")
        .checkTextBrick(0, 0, "[")
        .checkSpaceBrick(0, 1)
        .checkTextBrick(0, 2, "]")
        .checkTextBrick(0, 3, "");
  }

  @Test
  public void testDeleteCornerstoneCourseJoin() {
    /*
    Conditions
    1. Remove primitive, since it clears bricks and getFirst/Last brick will cause bounds error
    2. Removing primitive causes join:
    	2.1 Primitive is first in course
    	2.2 Primitive followed by something
    3. Primitive brick in join was cornerstone; join resets cornerstone so removed brick is readded
    4. Selection in parent (primitive not selected) so attachments survive join.
    5. Selection attachment base is primitive.
     */
    final FreeAtomType one =
        new TypeBuilder("one")
            .back(Helper.buildBackPrimitive("one"))
            .front(new FrontMarkBuilder("one").build())
            .precedence(20)
            .build();
    final FreeAtomType text =
        new TypeBuilder("text")
            .back(Helper.buildBackDataPrimitive("value"))
            .frontDataPrimitive("value")
            .precedence(10)
            .build();
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(text)
            .type(one)
            .group("any", new GroupBuilder().type(text).type(one).build())
            .style(new StyleBuilder().tag(new TypeTag("text")).split(true).build())
            .build();
    final Atom atom = new TreeBuilder(text).add("value", "alo").build();
    new GeneralTestWizard(syntax, new TreeBuilder(one).build(), atom, new TreeBuilder(one).build())
        .run(
            context -> {
              Helper.rootArray(context.document).select(context, true, 1, 1);
            })
        .checkCourseCount(2)
        .run(
            context ->
                context.history.apply(
                    context,
                    new ChangeArray(Helper.rootArray(context.document), 1, 1, ImmutableList.of())));
  }
}
